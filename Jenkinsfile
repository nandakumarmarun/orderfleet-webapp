pipeline {
    agent{
        label "master"
    }
    environment {
     test_server_ip = "13.232.79.102"
     test_server_user = "devops-user"
   }

    parameters {
        string(name: 'RELEASE_NO', defaultValue: '1.101.0', description: 'Release Number')
    }

    stages{
        stage("Running Script file") {
            steps {
                sh'''
                    PGPASSWORD=snrichpg2022 psql -h ${test_server_ip} -p 5432 -U postgres -f ./src/main/releases/'''+params.RELEASE_NO+'''/db-scripts.sql
                '''
            }
        }

        stage("validation") {
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


        stage("build war file") {
            tools {
                jdk "java8"
            }
            steps {
                sh'''
                    java -version
                    mvn clean package
                '''
            }ssh
        }

        stage("ssh") {
            steps {
                sshagent(['58453ca2-20ca-43ec-9283-c0e12d432741']) {
                    sh 'ssh -o StrictHostKeyChecking=no -l ${test_server_user} ${test_server_ip} mkdir -p /opt/test-salesnrich/'+ params.RELEASE_NO+ ' '
                    // create directory
                    sh 'scp ./target/orderfleet-webapp-'+params.RELEASE_NO+'.war ${test_server_user}@${test_server_ip}:/opt/test-salesnrich/'+ params.RELEASE_NO+ ' '
                }
            }
        }

        stage("running war file") {
            steps {
                sshagent(['58453ca2-20ca-43ec-9283-c0e12d432741']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no -l ${test_server_user} ${test_server_ip} 'sudo lsof -t -i tcp:80 -s tcp:listen | sudo xargs kill &'
                        ssh -o StrictHostKeyChecking=no -l ${test_server_user} ${test_server_ip} 'cd /opt/test-salesnrich/ && sudo nohup bash -c "java -jar ./'''+params.RELEASE_NO+'''/orderfleet-webapp-'''+params.RELEASE_NO+'''.war --spring.profiles.active=prod -Dspring.config.location=file:./application-prod.yml > service.out 2> errors.txt < /dev/null &" && sleep 4'
                    '''
                }
            }
        }
    }
}