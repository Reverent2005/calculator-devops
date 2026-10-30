pipeline {
    agent any

    triggers {
        // Configure this GitHub webhook: https://JENKINS_URL/github-webhook/
        githubPush()
    }

    environment {
        IMAGE_REPOSITORY = 'reverent/calculator-api'
        COMPOSE_PROJECT_NAME = 'calculator-devops'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Build') {
            steps { sh 'mvn -B clean package -DskipTests' }
        }
        stage('Test') {
            steps { sh 'mvn -B test' }
            post {
                always { junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false }
            }
        }
        stage('Docker Build') {
            steps {
                script {
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}"
                    sh 'docker build --pull -t $IMAGE_REPOSITORY:$IMAGE_TAG -t $IMAGE_REPOSITORY:latest .'
                }
            }
        }
        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    sh '''#!/bin/sh
                        set +x
                        echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                        docker push "$IMAGE_REPOSITORY:$IMAGE_TAG"
                        docker push "$IMAGE_REPOSITORY:latest"
                        docker logout
                    '''
                }
            }
        }
        stage('Deploy') {
            steps {
                sh '''#!/bin/sh
                    export CALCULATOR_IMAGE="$IMAGE_REPOSITORY:$IMAGE_TAG"
                    export APP_VERSION="$IMAGE_TAG"
                    docker compose pull calculator-api nginx
                    docker compose up -d --remove-orphans
                    for attempt in $(seq 1 12); do
                      if curl --fail --silent http://localhost:${APP_PORT:-8080}/actuator/health | grep -q '"status":"UP"'; then
                        exit 0
                      fi
                      sleep 5
                    done
                    docker compose ps
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            emailext(
                to: 'YOUR_STUDENT_EMAIL@example.com',
                subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Calculator API deployment succeeded. Build: ${env.BUILD_URL}"
            )
        }
        failure {
            emailext(
                to: 'YOUR_STUDENT_EMAIL@example.com',
                subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Calculator API pipeline failed. Review: ${env.BUILD_URL}"
            )
        }
        always { cleanWs(deleteDirs: true, notFailBuild: true) }
    }
}
