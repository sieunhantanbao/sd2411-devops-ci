#!/usr/bin/env groovy
void call() {
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

    // TODO: Add Security check
    // TODO: Scan unit test and report
    // TODO: Scan Sonarq and report

    stage ("Build Backend API") {
        dir("./src/ContainerApp.TodoApi"){
            docker.build("${dockerRegistry}/${backend}:${BUILD_NUMBER}", "--force-rm --no-cache -f Dockerfile .")
        }
    }

    stage ("Build Frontend") {
        dir("./src/ContainerApp.WebApp"){
            docker.build("${dockerRegistry}/${frontend}:${BUILD_NUMBER}", "--force-rm --no-cache -f Dockerfile .")
        }
    }

    stage ("Push Docker Images to ACR - backend") {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'acrcredential', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            docker.withRegistry("https://${dockerRegistry}", 'acrcredential' ) {
                sh "docker login ${dockerRegistry} -u ${USERNAME} -p ${PASSWORD}"
                sh "docker push ${dockerRegistry}/${backend}:${BUILD_NUMBER}"
            }
        }
    }

    stage ("Push Docker Images to ACR - frontend") {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'acrcredential', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            docker.withRegistry("https://${dockerRegistry}", 'acrcredential' ) {
                sh "docker login ${dockerRegistry} -u ${USERNAME} -p ${PASSWORD}"
                sh "docker push ${dockerRegistry}/${frontend}:${BUILD_NUMBER}"
            }
        }
    }
    stage ("Clean up docker images") {
       sh "docker rmi ${dockerRegistry}/${backend}:${BUILD_NUMBER}"
       sh "docker rmi ${dockerRegistry}/${frontend}:${BUILD_NUMBER}"
    }
}

//========================================================================
// SD2411_MSA CI
// Version: v1.0
// Updated:
//========================================================================
//========================================================================
// Notes:
//
//
//========================================================================