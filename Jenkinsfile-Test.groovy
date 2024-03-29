pipeline {
    agent{
        label "master"
    }
    environment {
     test_server_ip = "3.110.42.45"
     test_server_user = "devops-user"
   }

    parameters {
        string(name: 'RELEASE_NO', defaultValue: '1.101.0', description: 'Release Number')
    }

    stages{
        
        stage("Current-Version") {
            steps {
                echo params.RELEASE_NO
            }
        }

        // stage("Validation") {
        //     steps {
        //         script {
        //             res = sh (
        //                 script: 'PGPASSWORD=snrichpg2022 psql -t -h ${test_server_ip} -p 5432 -U postgres -d snrich -f ./src/main/releases/'+params.RELEASE_NO+'/verification-scripts.sql', 
        //                 returnStdout: true
        //                 ).trim()
        //             echo res
        //             if ("true".equals(res)) {
        //                 println "Validation success";    
        //             } else {
        //                 println "Validation failed"
        //                 error("Validation Failed: cannot execute query")
        //             }
        //         }
        //     }
        // }

        stage("Stop-Current-Application") {
            steps {
                sshagent(['58453ca2-20ca-43ec-9283-c0e12d432741']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no -l ${test_server_user} ${test_server_ip} 'sudo lsof -t -i tcp:80 -s tcp:listen | sudo xargs kill &'
                    '''
                }
            }
        }

        stage("DB-Update") {
            steps {
                sh'''
                    PGPASSWORD=admin123sn psql -h salesnrichtestdb-instance-1.cekzt74pbkzf.ap-south-1.rds.amazonaws.com -p 5432 -U postgres -d testsnrich -f ./src/main/releases/'''+params.RELEASE_NO+'''/db-scripts.sql
                '''
            }
        }

        stage("Src-Validate") {
            steps{
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def pomVersion = pom.getVersion();
                    def releaseNumber = params.RELEASE_NO
                    println("Pom version: "+ pomVersion + " Release Number: "+ releaseNumber);
                    if (releaseNumber.equals(pomVersion)) {
                        echo "Validation completed"
                    } else {
                        error("Build Failed: change pom version")
                    }
                }
            }
        }


        stage("Build-War") {
            tools {
                jdk "java8"
            }
            steps {
                sh'''
                    java -version
                    mvn clean package
                '''
            }
        }

        stage("Copy-War") {
            steps {
                sshagent(['58453ca2-20ca-43ec-9283-c0e12d432741']) {
                    sh 'ssh -o StrictHostKeyChecking=no -l ${test_server_user} ${test_server_ip} mkdir -p /opt/test-salesnrich/'+ params.RELEASE_NO+ ' '
                    // create directory
                    sh 'scp ./target/orderfleet-webapp-'+params.RELEASE_NO+'.war ${test_server_user}@${test_server_ip}:/opt/test-salesnrich/'+ params.RELEASE_NO+ ' '
                }
            }
        }

        stage("Deploy") {
            steps {
                sshagent(['58453ca2-20ca-43ec-9283-c0e12d432741']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no -l ${test_server_user} ${test_server_ip} 'cd /opt/test-salesnrich/ && sudo nohup bash -c "java -jar ./'''+params.RELEASE_NO+'''/orderfleet-webapp-'''+params.RELEASE_NO+'''.war --spring.profiles.active=test -Dspring.config.location=file:./application-test.yml > service.out 2> errors.txt < /dev/null &" && sleep 4'
                    '''
                }
            }
        }
    }
}