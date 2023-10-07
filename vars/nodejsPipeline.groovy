#!/usr/bin/env groovy
void call() {
    String backend = "backend"
    String frontend = "frontend"
    String dockerRegistry = "acranhnguyens.azurecr.io"
//========================================================================
//========================================================================

//========================================================================
//========================================================================

    stage ('Copy Dockerfiles to the source') {
        script {
            writeFile file: 'backend/Dockerfile.Backend', text: libraryResource('dev/docker/Dockerfile.Backend')
            writeFile file: 'frontend/Dockerfile.Frontend', text: libraryResource('dev/docker/Dockerfile.Frontend')
        }
    }

    stage ("Build Backend") {
        docker.build("${dockerRegistry}/${backend}:${BUILD_NUMBER}", "--force-rm --no-cache -f ./backend/Dockerfile.Backend .")
    }

    stage ("Build Frontend") {
        docker.build("${dockerRegistry}/${frontend}:${BUILD_NUMBER}", "--force-rm --no-cache -f ./backend/Dockerfile.Frontend .")
    }

    stage ("Push Docker Images to ACR - backend") {
        withCredentials([[$class: 'ACRCredential', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            docker.withRegistry("https://${dockerRegistry}", acrCredential ) {
                sh "docker login ${dockerRegistry} -u ${USERNAME} -p ${PASSWORD}"
                sh "docker push ${dockerRegistry}/${backend}/${name}:${BUILD_NUMBER}"
            }
        }
    }

    stage ("Push Docker Images to ACR - frontend") {
        withCredentials([[$class: 'ACRCredential', credentialsId: acrCredential, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            docker.withRegistry("https://${dockerRegistry}", acrCredential ) {
                sh "docker login ${dockerRegistry} -u ${USERNAME} -p ${PASSWORD}"
                sh "docker push ${dockerRegistry}/${frontend}/${name}:${BUILD_NUMBER}"
            }
        }
    }
}

//========================================================================
// Demo CI
// Version: v1.0
// Updated:
//========================================================================
//========================================================================
// Notes:
//
//
//========================================================================