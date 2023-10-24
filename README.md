# SD2411 DEVOPS CI
## Overview
Welcome to the SD2411 DEVOPS Continues Integration (CI) repository. This repository contains the Jenkins Groovy files to get, build the source code from the [SD2411_MSA Repository](https://github.com/sieunhantanbao/sd2411_msa) and then push the artifacts (docker images) to Azure Container Registry (ACR). The Continues Deployment (CD) process will be handled in the [SD2411 Azure Infrastructure Repository](https://github.com/sieunhantanbao/sd2411_azure_infrastructure).

## Key Features
- Auto build when the code changes are pushed into the master branch of the [SD2411_MSA Repository](https://github.com/sieunhantanbao/sd2411_msa).
- Use [Trivy](https://trivy.dev/) to scan security and vulnerabilities.
- Push the docker images (frontend, backend) to the Azure Container Registry (ACR).

## Setup Jenkins Organization Folder
### Prerequisites
- Azure Container Registry (ACR) is provisioned (see [Provision Azure Container Registry (ACR)](https://github.com/sieunhantanbao/sd2411_azure_infrastructure#provision-azure-container-registry-acr))
- Jenkins is installed in a Virtual Machine (see [Provision Virtual Machine (VMs)](https://github.com/sieunhantanbao/sd2411_azure_infrastructure#provision-virtual-machine-vms))
### Setup Jenkins Organization Job
Please refer to the [Setup_Jenkins_Pipeline_Use_Trivy.docx](https://github.com/sieunhantanbao/sd2411-devops-ci/blob/main/Setup_Jenkins_Pipeline_Use_Trivy.docx) for the details.

## Demonstration
- Push the change to the master branch of the [SD2411_MSA Repository](https://github.com/sieunhantanbao/sd2411_msa)<br/>
![image info](./images/git_push_main_branch.png)
- Jenkins job/pipeline is automatically triggered
![image info](./images/jenkins_is_triggered.png)
- The build artifacts (docker images) are pushed to the ACR
![image info](./images/acr_backend.PNG)
![image info](./images/acr_frontend.PNG)