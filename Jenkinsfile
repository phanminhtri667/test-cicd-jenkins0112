pipeline {
    agent any

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
