pipeline {
    agent any

    tools {
        // Make sure JDK and Gradle are installed in Jenkins
        jdk 'OpenJDK-23'
        gradle 'Gradle-8'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/yourusername/eduspring.git'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/test/*.xml'
            archiveArtifacts artifacts: '**/build/libs/*.jar', allowEmptyArchive: true
        }
        success {
            echo 'Build and tests passed!'
        }
        failure {
            echo 'Something failed. Check the logs!'
        }
    }
}
