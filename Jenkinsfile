pipeline {
    agent any
    tools {
        jdk 'Java 11'
    }
    stages {
        stage ('Build') {
            steps {
              sh 'gradle clean shadowJar'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'build/libs/*.jar', followSymlinks: false
                }
            }
        }
    }
}
