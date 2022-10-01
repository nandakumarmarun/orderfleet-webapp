pipeline{
    agent{
        label "master"
    }
    stages{
        stage("checkout"){
            steps{
                echo "========executing checkout========"
            }
            post{
                always{
                    echo "========always========"
                }
                success{
                    echo "========checkout executed successfully========"
                }
                failure{
                    echo "========checkout execution failed========"
                }
            }
        }
    }
    post{
        always{
            echo "========always========"
        }
        success{
            echo "========pipeline executed successfully ========"
        }
        failure{
            echo "========pipeline execution failed========"
        }
    }
}