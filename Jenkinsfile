pipeline {
    agent any
    environment {
        GHCR = "ghcr.io"
        IMAGE_TAG = "${GIT_COMMIT}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "Running CI build..."
                sh 'cd fullstack-frontend && npm install && npm run build'
                sh 'cd fullstack-backend && mvn clean install'
            }
        }

        stage('Login to GHCR') {
            steps {
                script {
                    docker.withRegistry("https://${GHCR}", "github-token") {
                        echo "Logged in to GHCR"
                    }
                }
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                script {
                    sh 'docker build -t ${GHCR}/phanminhtri667/jenkins-frontend:${IMAGE_TAG} ./jenkins-frontend'
                    sh 'docker push ${GHCR}/phanminhtri667/jenkins-frontend:${IMAGE_TAG}'
                    sh 'docker build -t ${GHCR}/phanminhtri667/jenkins-backend:${IMAGE_TAG} ./jenkins-backend'
                    sh 'docker push ${GHCR}/phanminhtri667/jenkins-backend:${IMAGE_TAG}'
                }
            }
        }

        stage('Deploy to VM-master-2') {
            steps {
                sshagent(['gcp-worker-ssh']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no pmt@35.202.204.24 "
                           
                            docker-compose down || true

                            cd /home/pmt/testCICDjenkins || git clone https://github.com/phanminhtri667/testCICDjenkins
                            cd /home/pmt/testCICDjenkins

                            git pull

                            docker-compose build

                            docker-compose up -d
                        "
                    '''
                }
            }
        }
    }
}
