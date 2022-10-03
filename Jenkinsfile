pipeline {
    agent{
        label "master"
    }

    parameters {
        string(name: 'RELEASE_NO', defaultValue: '1.101.0', description: 'Release Number')
    }

    stages{
        // stage("checkout"){
        //     steps{
        //         echo "========executing checkout========"
        //     }
        //     post{
        //         always{
        //             echo "========always========"
        //         }
        //         success{
        //             echo "========checkout executed successfully========"
        //         }
        //         failure{
        //             echo "========checkout execution failed========"
        //         }
        //     }
        // }

        // stage("parameters") {
        //     steps{
        //         echo "Release Number:  ${params.RELEASE_NO}"
        //     }
        // }

        // stage("validations") {
        //     steps{
        //         echo "Validations"
        //         sh'''
        //         ls -la
        //         '''
        //         script {
        //             def data = readFile(file: 'pom.xml')
        //             // println(data)

        //         }
        //     }
        // }

        // stage("read pom.xml") {
        //     steps{
        //         script {
        //         def pom = readMavenPom file: 'pom.xml'
        //         def pomVersion = pom.getVersion();
        //         def releaseNumber = params.RELEASE_NO
        //         println(params.RELEASE_NO);
        //         println(releaseNumber.equals("1.101.0"))

        //         if (releaseNumber.equals("1.101.0")) {
        //             echo "Success"
        //         } else {
        //             error("Build failed because of this and that..")
        //         }

        //         println(pom)

        //         }
        //     }
        // }


        // stage("build war file") {
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

        // stage("ssh") {
        //     steps {
        //         sshagent(['9e7473c2-7976-4fbf-9f49-badc35ce1538']) {
        //             sh'''
        //             ssh -o StrictHostKeyChecking=no -l ec2-user 13.232.79.102 'whoami'
        //             ls
        //             ls /home
        //         '''
        //         // create directory
        //         sh 'scp ./target/orderfleet-webapp-0.0.1-SNAPSHOT.war ec2-user@13.232.79.102:/home/ec2-user/home/deployments/new/'
        //         }
        //     }
        // }

        stage("running war file") {
            steps {
                sshagent(['9e7473c2-7976-4fbf-9f49-badc35ce1538']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no -l ec2-user 13.232.79.102 'kill `ps -ef | awk '/nohup/{ print \$2 }'`'
                        ssh -o StrictHostKeyChecking=no -l ec2-user 13.232.79.102 'cd /home/ec2-user/deploy/test-salesnrich/ && sudo nohup java -jar ./orderfleet-webapp-0.0.1-SNAPSHOT.war --spring.profiles.active=prod -Dspring.config.location=file:./application-prod.yml > service.out 2> errors.txt < /dev/null &'
                    '''
                }
            }
        }
    }
}