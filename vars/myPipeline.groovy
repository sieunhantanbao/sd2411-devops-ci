#!/usr/bin/env groovy

//========================================================================
// SD2411_MSA CI
// Version: v1.0
// Source code: https://github.com/sieunhantanbao/sd2411_msa
//========================================================================
//========================================================================
// Notes:
//
//
//========================================================================

void call(Map pipelineParams) {

    pipeline {

        agent any

        options {
            disableConcurrentBuilds()
            disableResume()
            timeout(time: 1, unit: 'HOURS')
        }
        
        stages {
            stage ('Load Pipeline') {
                when {
                    allOf {
                        // Condition Check
                        anyOf{
                            // Branch Event: Nornal Flow
                            anyOf {
                                branch 'main'
                                branch 'master'
                                // branch 'jenkins'
                                // branch 'PR-*'
                            }
                            // Manual Run: Only if checked.
                            allOf{
                                triggeredBy 'UserIdCause'
                            }
                        }
                    }
                }
                steps {
                    script {
                        dotnet()
                    }
                }
            }
        }

        post {
            cleanup {
                cleanWs()
            }
        }
    }
}