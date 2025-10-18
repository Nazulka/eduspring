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
                git branch: 'feat/frontEnd2', url: 'https://github.com/Nazulka/eduspring.git'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Test') {
            steps {
            // run unit tests
                sh './gradlew test'
            }
        }
        stage('Code Coverage') {
                    steps {
                        // Generate coverage report
                        sh './gradlew jacocoTestReport'
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
