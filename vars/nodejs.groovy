#!/usr/bin/env groovy
void call() {
    String backend = "backend"
    String frontend = "frontend"
    String dockerRegistry = "acranhnguyens.azurecr.io"
//========================================================================
//========================================================================

//========================================================================
//========================================================================

    stage ('Prepare packages') {
        script {
            writeFile file: '.ci/trivy_report.tpl', text: libraryResource('templates/trivy_report.tpl')
        }
    }

    stage ("Trivy Scan secret") {
        script {
            sh "trivy fs . --scanners secret --exit-code 0 --format template --template @.ci/trivy_report.tpl -o .ci/secretreport.html"
            publishHTML(target: [allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: '.ci',
                reportFiles: 'secretreport.html',
                reportName: 'Trivy Secret Report',
                reportTitles:  'Trivy Secret Report'
            ])
        }
    }
    stage ("Trivy Scan Vulnerabilities") {
        script {
            sh "trivy fs . --severity HIGH,CRITICAL --scanners vuln --exit-code 0 --format template --template @.ci/trivy_report.tpl -o .ci/vulnreport.html"
            publishHTML(target: [allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: '.ci',
                reportFiles: 'vulnreport.html',
                reportName: 'Trivy Vulnerabilities Report',
                reportTitles:  'Trivy Vulnerabilities Report'
            ])
        }
    }

    stage ("Build Backend") {
        dir("./src/backend"){
            docker.build("${dockerRegistry}/${backend}:${BUILD_NUMBER}", "--force-rm --no-cache -f Dockerfile .")
        }
    }

    stage ("Build Frontend") {
        dir("./src/frontend"){
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