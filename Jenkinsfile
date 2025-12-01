pipeline {
    agent any
    environment {
        // GHCR registry URL
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
                // Build ứng dụng của bạn, ví dụ cho Node.js frontend và Java backend
                sh 'cd fullstack-frontend && npm install && npm run build'
                sh 'cd fullstack-backend && mvn clean install'
            }
        }

        stage('Login to GHCR') {
            steps {
                script {
                    // Đăng nhập vào GHCR
                    docker.withRegistry("https://${GHCR}", "github-token") {
                        echo "Logged in to GHCR"
                    }
                }
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                script {
                    // Build Docker image cho frontend
                    sh 'docker build -t ${GHCR}/phanminhtri667/jenkins-frontend:${IMAGE_TAG} ./jenkins-frontend'
                    // Push Docker image cho frontend lên GHCR
                    sh 'docker push ${GHCR}/phanminhtri667/jenkins-frontend:${IMAGE_TAG}'

                    // Build Docker image cho backend
                    sh 'docker build -t ${GHCR}/phanminhtri667/jenkins-backend:${IMAGE_TAG} ./jenkins-backend'
                    // Push Docker image cho backend lên GHCR
                    sh 'docker push ${GHCR}/phanminhtri667/jenkins-backend:${IMAGE_TAG}'
                }
            }
        }

        stage('Deploy to VM-master-2') {
            steps {
                sshagent(['gcp-worker-ssh']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no pmt@35.202.204.24 "
                            # Xóa ứng dụng cũ (nếu có)
                            docker-compose down || true

                            # Clone mã nguồn nếu chưa có
                            cd /home/pmt/testCICDjenkins || git clone https://github.com/phanminhtri667/testCICDjenkins
                            cd /home/pmt/testCICDjenkins

                            # Pull latest code
                            git pull

                            # Build lại Docker images cho frontend và backend
                            docker-compose build

                            # Chạy lại Docker containers
                            docker-compose up -d
                        "
                    '''
                }
            }
        }
    }
}
