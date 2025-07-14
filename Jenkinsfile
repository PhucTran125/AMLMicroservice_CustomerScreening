pipeline {
    agent any
    environment {
        DOCKER_IMAGE = "vpbankhackathon/aml-customer-screening:${env.BUILD_NUMBER}"
        AWS_REGION = "ap-southeast-1"
        ECR_REGISTRY = "711652947591.dkr.ecr.ap-southeast-1.amazonaws.com"
        AWS_CREDENTIALS = 'your-aws-credentials-id' //
    }
    triggers {
        githubPush()
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    git url: 'https://github.com/PhucTran125/AMLMicroservice_CustomerScreening', branch: 'master'
                }
            }
        }
        stage('Build') {
            steps {
                bat 'mvnw clean package -DskipTests'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build(DOCKER_IMAGE, '.')
                }
            }
        }
        stage('Push to ECR') {
            when {
                branch 'master'
            }
            steps {
                script {
                    docker.withRegistry("https://${ECR_REGISTRY}", "ecr:${AWS_REGION}:${AWS_CREDENTIALS}") {
                        docker.image(DOCKER_IMAGE).push()
                        docker.image(DOCKER_IMAGE).push('latest')
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'Pipeline completed successfully! Image pushed to ECR.'
        }
        failure {
            echo 'Pipeline failed!'
        }
        always {
            bat "docker rmi ${DOCKER_IMAGE} || true" // Thay sh bằng bat
        }
    }
}