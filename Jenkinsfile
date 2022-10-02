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

        stage("validations") {
            steps{
                echo "Validations"
                sh'''
                ls -la
                '''
                script {
                    def data = readFile(file: 'pom.xml')
                    // println(data)

                }
            }
        }

        stage("read pom.xml") {
            steps{
                script {
                def pom = readMavenPom file: 'pom.xml'
                def version = pom.getVersion();
                println(${params.RELEASE_NO});
                println(version.equals("1.101.0"))

                println(pom)
//                     def settings = load('pom.xml')
//                     echo "version: ${project.parent.version}"
                }
            }
        }
    }
}