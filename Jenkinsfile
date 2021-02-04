pipeline {
    agent any
    tools {
        jdk 'Java 11'
    }
    stages {
        stage ('Build') {
            steps {
              sh 'gradle clean shadowJar -PbuildNumber=${BUILD_NUMBER} -Pbranch=${GIT_BRANCH}'
            }
            post {
                success {
                  archiveArtifacts artifacts: 'client/build/libs/, server/build/libs/', followSymlinks: false                
                }
            }
        }
    }
}
