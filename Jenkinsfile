pipeline {
    agent any

    tools {
        // Names must match Manage Jenkins > Tools
        maven 'Maven3'
        jdk   'JDK17'
    }

    environment {
        DOCKER_IMAGE = 'YOUR_DOCKERHUB_USERNAME/calculator-devops'   // <-- change me
        IMAGE_TAG    = "${env.BUILD_NUMBER}"
        NOTIFY_EMAIL = 'YOUR_EMAIL@example.com'                      // <-- change me
        PROXY_PORT   = '8081'
        // Jenkins credentials (type: Username with password), ID = dockerhub-creds
        DOCKERHUB    = credentials('dockerhub-creds')
    }

    triggers {
        githubPush()          // GitHub webhook; use pollSCM('H/2 * * * *') if no public URL
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} -t ${DOCKER_IMAGE}:latest .
                '''
            }
        }

        stage('Push') {
            steps {
                sh '''
                    echo "$DOCKERHUB_PSW" | docker login -u "$DOCKERHUB_USR" --password-stdin
                    docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                    docker push ${DOCKER_IMAGE}:latest
                    docker logout
                '''
            }
        }

        stage('Deploy') {
            steps {
                // `up -d` only recreates containers whose image/config changed -> idempotent
                sh '''
                    export DOCKER_IMAGE IMAGE_TAG PROXY_PORT
                    docker compose pull
                    docker compose up -d --remove-orphans

                    echo "Waiting for health endpoint..."
                    for i in $(seq 1 30); do
                        if curl -fs http://localhost:${PROXY_PORT}/actuator/health | grep -q UP; then
                            echo "Deployment healthy"; docker compose ps; exit 0
                        fi
                        sleep 5
                    done
                    echo "Health check failed"; docker compose logs --tail=50; exit 1
                '''
            }
        }
    }

    post {
        success {
            mail to: "${NOTIFY_EMAIL}",
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build succeeded and was deployed.\nImage: ${DOCKER_IMAGE}:${IMAGE_TAG}\n${env.BUILD_URL}"
        }
        failure {
            mail to: "${NOTIFY_EMAIL}",
                 subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Build failed. See ${env.BUILD_URL}console"
        }
    }
}
