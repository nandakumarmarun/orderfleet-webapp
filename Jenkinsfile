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

        stage("Validation") {
            steps {
                sh'''
                     result = $(PGPASSWORD=snrichpg2022 psql -h ${test_server_ip} -p 5432 -U postgres -f ./src/main/releases/'''+params.RELEASE_NO+'''/verification-scripts.sql)
                     echo $result;
                 '''
            }
        }

        // stage("Current-Version") {
        //     steps {
        //         echo params.RELEASE_NO
        //     }
        // }

        // stage("Stop-Current-Application") {
        //     steps {
        //         sshagent(['58453ca2-20ca-43ec-9283-c0e12d432741']) {
        //             sh '''
        //                 ssh -o StrictHostKeyChecking=no -l ${test_server_user} ${test_server_ip} 'sudo lsof -t -i tcp:80 -s tcp:listen | sudo xargs kill &'
        //             '''
        //         }
        //     }
        // }

        // stage("DB-Update") {
        //     steps {
        //         sh'''
        //             PGPASSWORD=snrichpg2022 psql -h ${test_server_ip} -p 5432 -U postgres -f ./src/main/releases/'''+params.RELEASE_NO+'''/db-scripts.sql
        //         '''
        //     }
        // }

        // stage("Src-Validate") {
        //     steps{
        //         script {
        //             def pom = readMavenPom file: 'pom.xml'
        //             def pomVersion = pom.getVersion();
        //             def releaseNumber = params.RELEASE_NO
        //             println("Pom version: "+ pomVersion + " Release Number: "+ releaseNumber);
        //             if (releaseNumber.equals(pomVersion)) {
        //                 echo "Validation completed"
        //             } else {
        //                 error("Build Failed: change pom version")
        //             }
        //         }
        //     }
        // }


        // stage("Build-War") {
        //     tools {
        //         jdk "java8"
        //     }
        //     steps {
        //         sh'''
        //             java -version
        //             mvn clean package
        //         '''
        //     }
        // }

        // stage("Copy-War") {
        //     steps {
        //         sshagent(['58453ca2-20ca-43ec-9283-c0e12d432741']) {
        //             sh 'ssh -o StrictHostKeyChecking=no -l ${test_server_user} ${test_server_ip} mkdir -p /opt/test-salesnrich/'+ params.RELEASE_NO+ ' '
        //             // create directory
        //             sh 'scp ./target/orderfleet-webapp-'+params.RELEASE_NO+'.war ${test_server_user}@${test_server_ip}:/opt/test-salesnrich/'+ params.RELEASE_NO+ ' '
        //         }
        //     }
        // }

        // stage("Deploy") {
        //     steps {
        //         sshagent(['58453ca2-20ca-43ec-9283-c0e12d432741']) {
        //             sh '''
        //                 ssh -o StrictHostKeyChecking=no -l ${test_server_user} ${test_server_ip} 'cd /opt/test-salesnrich/ && sudo nohup bash -c "java -jar ./'''+params.RELEASE_NO+'''/orderfleet-webapp-'''+params.RELEASE_NO+'''.war --spring.profiles.active=test -Dspring.config.location=file:./application-test.yml > service.out 2> errors.txt < /dev/null &" && sleep 4'
        //             '''
        //         }
        //     }
        // }
    }
}