#!/usr/bin/env groovy
void call(Map pipelineParams) {
    String backend = "backend"
    String frontend = "frontend"
    String dockerRegistry = "acranhnguyens.azurecr.io"
//========================================================================
//========================================================================

//========================================================================
//========================================================================

    // stage ('Copy Dockerfiles to the source') {
    //     script {
    //         writeFile file: 'backend/Dockerfile.Backend', text: libraryResource('dev/docker/Dockerfile.Backend')
    //         writeFile file: 'frontend/Dockerfile.Frontend', text: libraryResource('dev/docker/Dockerfile.Frontend')
    //     }
    // }

    stage ("Build Backend") {
        sh "ls -la ${pwd()}"
        docker.build("${dockerRegistry}/${backend}:${BUILD_NUMBER}", "--force-rm --no-cache -f ${WORKSPACE}/backend/Dockerfile .")
    }

    stage ("Build Frontend") {
        docker.build("${dockerRegistry}/${frontend}:${BUILD_NUMBER}", "--force-rm --no-cache -f ${WORKSPACE}/frontend/Dockerfile .")
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