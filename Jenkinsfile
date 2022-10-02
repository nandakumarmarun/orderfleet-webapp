pipeline{
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
                script {
                    def data = readFile(file: '/src/main/releases/1.101.0/db-scripts.sql')
                    println(data)
                }
            }
        }
    }
}