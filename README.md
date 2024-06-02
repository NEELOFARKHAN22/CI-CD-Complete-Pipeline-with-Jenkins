# Table of Contents

- [Project Description](#project-description)
- [Technologies Used](#technologies-used)
- [Installation Instructions](#installation-instructions)
  - [Prerequisites](#prerequisites)
- [Solution Overview](#solution)
  - [1. Launching an AWS EC2 Instance](#launching-an-aws-ec2-instance)
  - [2. Installing Jenkins on EC2](#installing-jenkins-on-ec2)
  - [3. Launching a Pipeline Job in Jenkins](#launching-a-pipeline-job-in-jenkins)
  - [4. Installing MySQL on Server](#installing-mysql-on-server)
  - [5. Adding Pipeline Script to Jenkins Job](#adding-pipeline-script-to-jenkins-job)
  - [6. Setting Up AWS ECR Repository](#setting-up-aws-ecr-repository)
  - [7. IAM Role Setup: AmazonEC2ContainerRegistryFullAccess](#iam-role-setup-amazonec2containerregistryfullaccess)
- [Terraform Configuration with Jenkins](#terraform-configuration-with-jenkins)
    - [1. Install Terraform Plugins in Jenkins](#1-install-terraform-plugins-in-jenkins)
    - [2. Install AWS Plugins in Jenkins](#2-install-aws-plugins-in-jenkins)
    - [3. Add AWS Credentials](#3-add-aws-credentials)
    - [4. Integrate Terraform with Jenkins](#4-integrate-terraform-with-jenkins)
    - [5. Create Terraform Configuration Files](#5-create-terraform-configuration-files)
- [Terraform Configuration Files](#terraform-configuration-files)
    - [`main.tf`](#maintf)
    - [`providers.tf`](#providerstf)
    - [Terraform Script Explanation](#terraform-script-explanation)
- [Terraform Pipeline Script](#terraform-pipeline-script)
    - [Terraform Pipeline Script Explanation](#terraform-pipeline-script-explanation)
- [Commit and Push Changes](#commit-and-push-changes)
- [Execute Jenkins Job](#execute-jenkins-job)

  
## Project Description

Integrate the provisioning stage into a complete CI/CD pipeline to automate provisioning servers instead of deploying to an existing server. This project includes:

- Creating an SSH Key Pair
- Installing Terraform inside a Jenkins container
- Adding Terraform configuration to the application’s Git repository
- Adjusting the Jenkinsfile to add a “provision” step to the CI/CD pipeline that provisions an EC2 instance

The complete CI/CD project configuration includes:

1. **CI step**: Build artifact for Java Maven application
2. **CI step**: Build and push Docker image to Docker Hub
3. **CD step**: Automatically provision EC2 instance using Terraform
4. **CD step**: Deploy new application version on the provisioned EC2 instance with Docker Compose

## Technologies Used

| Technology     | Description                                        |
|----------------|----------------------------------------------------|
| Terraform      | Infrastructure as Code tool for provisioning      |
| Jenkins        | Automation server for CI/CD pipelines              |
| Docker         | Containerization platform for packaging applications|
| AWS            | Cloud computing services for infrastructure        |
| Git            | Version control system for source code management  |
| Java           | Programming language for application development  |
| Maven          | Build automation tool for Java projects           |
| Linux          | Operating system for server environments          |
| Docker Hub     | Registry for Docker images                         |
| AWS ECR        | Amazon Elastic Container Registry for Docker images|


## Installation Instructions

### Prerequisites

Ensure you have the following prerequisites installed:

| Step       | Description                                        |
|------------|----------------------------------------------------|
| Docker     | Containerization platform for packaging applications|
| Git        | Version control system for source code management  |
| AWS CLI    | Command-line tool for interacting with AWS services|
| Java       | Programming language for application development  |
| Maven      | Build automation tool for Java projects           |


# Solution

## Launching an AWS EC2 Instance

1. **Log in to AWS Management Console**
   - Go to [AWS Management Console](https://aws.amazon.com/console/).
   - Log in with your credentials.

2. **Navigate to EC2 Dashboard**
   - Click on "Services" in the top menu and select "EC2" under "Compute".

3. **Launch Instance**
   - Click on "Launch Instance".

4. **Choose an Amazon Machine Image (AMI)**
   - Select "Ubuntu Server 22.04 LTS (HVM), SSD Volume Type".

5. **Choose Instance Type**
   - Select `t2.large` which has 2 vCPUs and 8GB of RAM.
   - Click "Next: Configure Instance Details".

6. **Configure Instance Details**
   - Leave all settings as default.
   - Click "Next: Add Storage".

7. **Add Storage**
   - The default storage size is sufficient.
   - Click "Next: Add Tags".

8. **Add Tags**
   - (Optional) Add a tag with Key: `Name` and Value: `JenkinsServer`.
   - Click "Next: Configure Security Group".

9. **Configure Security Group**
   - Create a new security group.
   - Add the following rules:
     - SSH: Port 22, Source: My IP
     - HTTP: Port 80, Source: Anywhere
     - Custom TCP: Port 8080, Source: Anywhere (for Jenkins)
   - Click "Review and Launch".

10. **Review and Launch**
    - Review your instance settings.
    - Click "Launch".
    - Select an existing key pair or create a new key pair to access the instance.
    - Click "Launch Instances".

11. **Access Your Instance**
    - Once the instance is running, click on "View Instances".
    - Note the Public DNS (IPv4) of your instance.
    - Use the following command to SSH into your instance:

    ```bash
    ssh -i /path/to/your-key.pem ubuntu@your-instance-public-dns
    ```

## Installing Jenkins on EC2

1. **Update Your System**

    ```bash
    sudo apt update
    sudo apt upgrade -y
    ```

2. **Install Java**

    ```bash
    sudo apt install openjdk-11-jdk -y
    ```

3. **Add Jenkins Repository**

    ```bash
    wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
    sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
    sudo apt update
    ```

4. **Install Jenkins**

    ```bash
    sudo apt install jenkins -y
    ```

5. **Start Jenkins**

    ```bash
    sudo systemctl start jenkins
    ```

6. **Enable Jenkins to Start on Boot**

    ```bash
    sudo systemctl enable jenkins
    ```

7. **Adjust Firewall**

    ```bash
    sudo ufw allow 8080
    sudo ufw allow OpenSSH
    sudo ufw enable
    ```

8. **Access Jenkins**

    - Open a web browser and go to `http://your-instance-public-dns:8080`.
    - Retrieve the initial admin password:

    ```bash
    sudo cat /var/lib/jenkins/secrets/initialAdminPassword
    ```

    - Copy the password and paste it into the Jenkins setup wizard.
    - Follow the setup wizard to install suggested plugins and create your first admin user.
      
## Launching a Pipeline Job in Jenkins

1. **Install Pipeline Plugins**
   - Before creating the pipeline job, ensure that all necessary plugins related to pipeline jobs are installed in Jenkins.

2. **Create a New Pipeline Job**
   - Go to Jenkins dashboard.
   - Click on "New Item".
   - Enter a name for your job and select "Pipeline" as the job type.
   - Click "OK".
  
## Configure Pipeline Job and Add GitHub Webhook

### Configure Pipeline Job

Under the "Pipeline" section of your Jenkins job configuration:

1. **Choose "Pipeline script from SCM"**
   - Select Git as the SCM.

2. **Provide GitHub Repository URL**
   - Enter your GitHub repository URL: [https://github.com/NEELOFARKHAN22/CI-CD-Complete-Pipeline-with-Jenkins/](https://github.com/NEELOFARKHAN22/CI-CD-Complete-Pipeline-with-Jenkins/).

3. **Add GitHub Webhook**
   - To automatically trigger the Jenkins job on code push events, we'll set up a webhook in your GitHub repository. Follow these steps:

   ### Adding GitHub Webhook

   1. **Go to GitHub Repository Settings**
      - Navigate to your GitHub repository: [https://github.com/NEELOFARKHAN22/CI-CD-Complete-Pipeline-with-Jenkins/](https://github.com/NEELOFARKHAN22/CI-CD-Complete-Pipeline-with-Jenkins/).
      - Click on the "Settings" tab.

   2. **Navigate to Webhooks Settings**
      - On the left sidebar, click on "Webhooks".
      - Click on the "Add webhook" button.

   3. **Configure Webhook**
      - In the "Payload URL" field, enter the URL of your Jenkins server's webhook endpoint. It should be in the format: `http://your-jenkins-server/github-webhook/`.
      - Set the "Content type" to `application/json`.
      - In the "Which events would you like to trigger this webhook?" section, select "Just the push event".

   4. **Add Webhook**
      - Click on the "Add webhook" button to save your webhook configuration.

Now, your Jenkins job will be triggered automatically whenever a code push event occurs in your GitHub repository.

## Installing MySQL on Server

To set up MySQL on your server where Jenkins is running, follow these steps:

1. **Install MySQL**
   - Open a terminal on your server.
   - Run the following command to install MySQL:
     ```bash
     sudo apt update
     sudo apt install mysql-server
     ```

2. **Configure MySQL**
   - During the installation process, you will be prompted to set a root password for MySQL. Enter a strong password and remember it.
   - After the installation is complete, run the following command to secure your MySQL installation:
     ```bash
     sudo mysql_secure_installation
     ```
   - Follow the prompts to configure MySQL security settings. You can choose to set up a root password, remove anonymous users, disallow root login remotely, and remove the test database.
   - If you choose to set up a root password during the secure installation process, remember to use this password in subsequent steps.

3. **Set Up MySQL User and Password**
   - Log in to the MySQL root account using the following command:
     ```bash
     sudo mysql
     ```
   - Once logged in, execute the following SQL commands to set up a MySQL user and password that will be used by your Java Maven application:
     ```sql
     ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Nishant@123';
     exit
     ```
   - Log in to MySQL again, this time using the new password:
     ```bash
     mysql -u root -p
     ```
   - Enter the password when prompted.
   - After logging in, execute the following SQL command to update the user authentication method:
     ```sql
     ALTER USER 'root'@'localhost' IDENTIFIED WITH auth_socket;
     ```

Now, MySQL is installed and configured on your server, and you have set up a MySQL user and password for your Java Maven application.

For detailed instructions, you can refer to [How To Install MySQL on Ubuntu 20.04](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-20-04) on DigitalOcean's website.

## Adding Pipeline Script to Jenkins Job

To automate the build, test, dockerization, and deployment process in your Jenkins job, you can use the provided pipeline script. Follow these steps to configure your Jenkins job with the pipeline script:

1. **Access Jenkins Dashboard**
   - Open your web browser and navigate to your Jenkins dashboard.

2. **Create a New Jenkins Job**
   - Click on "New Item" or "Create New Job" to create a new Jenkins job.
   - Enter a name for your job and select "Pipeline" as the job type.
   - Click on "OK" to proceed.

3. **Configure Pipeline Job**
   - Scroll down to the "Pipeline" section of your job configuration.
   - Select "Pipeline script from SCM" as the definition.
   - Choose Git as the SCM and provide the URL of your GitHub repository: [https://github.com/NEELOFARKHAN22/CI-CD-Complete-Pipeline-with-Jenkins/](https://github.com/NEELOFARKHAN22/CI-CD-Complete-Pipeline-with-Jenkins/).

4. **Add Pipeline Script**
   - Copy the provided pipeline script and paste it into the pipeline script editor.
   - This script automates the following stages:
     - **Checkout**: Clones the repository.
     - **Build**: Compiles the Java Maven application and runs tests.
     - **Dockerize Application**: Creates a Docker image for the application.
     - **Push to ECR**: Tags and pushes the Docker image to the AWS ECR repository.
   - Save the changes to your job configuration.

5. **Understanding Pipeline Script**

```groovy
pipeline {
    agent any

    environment {
        DB_USERNAME = 'root'
        DB_PASSWORD =  'Nishant@123'
        AWS_REGION = 'us-east-1'
        ECR_REPOSITORY = '642534338961.dkr.ecr.us-east-1.amazonaws.com/java-meven'
        IMAGE_TAG = 'latest'
        AWS_ACCOUNT_ID='642534338961'
    }   

    tools {
        maven "M3"
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/NEELOFARKHAN22/CI-CD-Complete-Pipeline-with-Jenkins.git'
            }
        }
        stage('Build') {
            steps {
                dir('/var/lib/jenkins/workspace/CI-CD-Pipeline') {
                    script {
                        def retries = 3
                        def attempt = 1
                        
                        while (attempt <= retries) {
                            try {
                                sh "mvn -Dmaven.test.failure.ignore=true -Ddb.username=${DB_USERNAME} -Ddb.password=${DB_PASSWORD} clean package"
                                break
                            } catch (Exception e) {
                                echo "Attempt ${attempt} failed. Retrying..."
                                attempt++
                            }
                        }
                    }
                }
            }
            post {
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
                unstable {
                    echo "Build is unstable. Investigate test failures."
                }
            }
        }
        
        stage('Dockerize Application') {
            steps {
                script {
                    writeFile file: 'Dockerfile', text: '''
                    FROM openjdk:8-jdk-alpine
                    WORKDIR /app
                    COPY target/rest-0.0.1-SNAPSHOT.jar app.jar
                    CMD ["java", "-jar", "app.jar"]
                    '''
                    
                    sh 'docker build -t java-maven .'
                }
            }
        }

        stage('Push to ECR') {
            steps {
                script {
                    sh 'aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 642534338961.dkr.ecr.us-east-1.amazonaws.com'
                    sh 'docker push 642534338961.dkr.ecr.us-east-1.amazonaws.com/java-meven:latest'
                }
            }
        }
    }
}
```
6. **Understanding Pipeline Script**
   - **Agent any**: Specifies that the pipeline can run on any available agent in Jenkins.
   - **Environment**: Defines environment variables used throughout the pipeline.
   - **Tools**: Specifies the Maven tool installation to be used.
   - **Stages**: Defines the different stages of the pipeline.
     - Each stage consists of one or more steps, executed sequentially.
   - **Steps**:
     - **Checkout**: Clones the repository using Git.
     - **Build**: Compiles the Java Maven application. Retries up to 3 times if the build fails.
       - Script explanation:
         - Sets up retries to handle build failures gracefully.
     - **Dockerize Application**: Creates a Docker image for the application using a Dockerfile.
       - Script explanation:
         - Defines a Dockerfile to package the application.
         - Builds the Docker image.
     - **Push to ECR**: Tags and pushes the Docker image to the AWS ECR repository.
       - Script explanation:
         - Logs in to AWS ECR.
         - Tags the Docker image.
         - Pushes the Docker image to the ECR repository.

7. **Save and Run Job**
   - Save your Jenkins job configuration.
   - Run the job manually or wait for GitHub webhook to trigger the job automatically on code push events.

Now, your Jenkins job is configured to use the pipeline script for automating the CI/CD process of your Java Maven application.

8. **Setting Up AWS ECR Repository**

  To set up an AWS ECR repository for storing Docker images, follow these steps:

   - 1. **Access AWS Management Console**
        - Log in to your AWS Management Console.

   - 2. **Navigate to Amazon ECR**
        - Click on "Services" in the top menu and select "ECR" under "Containers".

   - 3. **Create ECR Repository**
        - Click on "Create repository".
        - Enter a name for your repository (e.g., java-meven).
        - Click on "Create repository" to create the repository.

   - 4. **Make Note of Repository URI**
        - Once the repository is created, make a note of the repository URI, which will be used in your Jenkins pipeline script.

  Now, you have successfully set up an AWS ECR repository to store Docker images for your application.
## IAM Role Setup: AmazonEC2ContainerRegistryFullAccess

To attach the `AmazonEC2ContainerRegistryFullAccess` IAM role to your instance, follow these steps:

1. **Access AWS Management Console**
   - Log in to your AWS Management Console.

2. **Navigate to IAM Service**
   - Click on "Services" in the top menu and select "IAM" under "Security, Identity, & Compliance".

3. **Create IAM Role**
   - In the IAM dashboard, click on "Roles" in the left sidebar.
   - Click on "Create role".
   
4. **Choose Service That Will Use Role**
   - Select "EC2" as the service that will use this role.
   - Click on "Next: Permissions".

5. **Attach Policy**
   - In the search box, type "AmazonEC2ContainerRegistryFullAccess" to find the policy.
   - Check the box next to "AmazonEC2ContainerRegistryFullAccess" to select the policy.
   - Click on "Next: Tags".

6. **Add Tags (Optional)**
   - Optionally, add tags to the IAM role for better organization.
   - Click on "Next: Review".

7. **Review and Create Role**
   - Review the role details.
   - Enter a name for the role (e.g., EC2ContainerRegistryFullAccessRole).
   - Click on "Create role".

8. **Attach Role to EC2 Instance**
   - Once the role is created, navigate to your EC2 instance.
   - Click on the instance and then click on "Actions".
   - Select "Instance Settings" > "Attach/Replace IAM Role".
   - Choose the IAM role you created (e.g., EC2ContainerRegistryFullAccessRole) from the dropdown menu.
   - Click on "Apply" to attach the role to your EC2 instance.

Now, your EC2 instance has the necessary IAM role (`AmazonEC2ContainerRegistryFullAccess`) attached, allowing it to access Amazon ECR without authentication credentials.

# Terraform Configuration with Jenkins

To configure Terraform with Jenkins for infrastructure provisioning, follow these steps:

1. **Install Terraform Plugins in Jenkins**
   - Log in to your Jenkins server.
   - Navigate to "Manage Jenkins" > "Manage Plugins".
   - Go to the "Available" tab and search for "Terraform" plugins.
   - Install the necessary Terraform plugins.

2. **Install AWS Plugins in Jenkins**
   - Similarly, install the AWS plugins in Jenkins if not already installed.
   - These plugins are required for Jenkins to interact with AWS services.

3. **Add AWS Credentials**
   - Go to "Manage Jenkins" > "Manage Credentials".
   - Add your AWS Access Key ID and Secret Access Key as Jenkins credentials.
   - These credentials will be used by Jenkins jobs to interact with AWS.

4. **Integrate Terraform with Jenkins**
   - Create a new Jenkins job or navigate to an existing one.
   - In the job configuration, add a build step to execute Terraform commands.
   - Configure the Terraform commands to initialize, plan, apply, or destroy infrastructure as needed.
   - Ensure that the AWS credentials are passed to Terraform securely.
5. **Create Terraform Configuration Files**
   - Create the necessary Terraform configuration files in your project directory.
   - These files typically include:
     - `main.tf`: Defines the main infrastructure resources.
     - `providers.tf`: Specifies the provider configuration, such as AWS.
     - `variables.tf`: Declares input variables used in the Terraform configuration.
     - `output.tf` : store ouputs.
## Terraform Configuration Files

### `main.tf`

```hcl
provider "aws" {
  region = "us-east-1"
}

resource "aws_iam_role" "ec2_role" {
  name = "ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy" "ecr_policy" {
  name = "ecr-policy"
  role = aws_iam_role.ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetAuthorizationToken"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_instance_profile" "ec2_instance_profile" {
  name = "ec2-instance-profile"
  role = aws_iam_role.ec2_role.name
}

resource "aws_key_pair" "deployer" {
  key_name   = var.key_name
  public_key = file("${path.module}/my-key.pub")
}

resource "aws_security_group" "network_security_group" {
  name        = var.network_security_group_name
  description = "Allow TLS inbound traffic"

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] 
  }
  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    ipv6_cidr_blocks = ["::/0"]
    cidr_blocks      = ["0.0.0.0/0"]
  }

  tags = {
    Name = "nsg-inbound"
  }
}

resource "aws_instance" "ubuntu_vm_instance" {
  ami                         = var.ubuntu_ami
  instance_type               = var.ubuntu_instance_type
  key_name                    = aws_key_pair.deployer.key_name
  vpc_security_group_ids      = [aws_security_group.network_security_group.id]
  iam_instance_profile        = aws_iam_instance_profile.ec2_instance_profile.name
  tags = {
    Name = "ubuntu-vm"
  }
  user_data = <<-EOF
                    #!/bin/bash
                    sudo apt-get update
                    sudo apt-get install -y ca-certificates curl unzip
                    sudo install -m 0755 -d /etc/apt/keyrings
                    sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
                    sudo chmod a+r /etc/apt/keyrings/docker.asc
                    echo \
                      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
                       $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
                        sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
                    sudo apt-get update
                    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
                    docker compose version
                    sudo apt-get install -y ca-certificates curl unzip
                    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                    unzip awscliv2.zip
                    sudo ./aws/install
                    sudo snap install aws-cli
                    aws ecr get-login-password --region us-east-1 | sudo docker login --username AWS --password-stdin 642534338961.dkr.ecr.us-east-1.amazonaws.com
                    sudo docker pull 642534338961.dkr.ecr.us-east-1.amazonaws.com/java-meven:latest
                    #sudo docker run -it --name container1 java-meven:latest
                    cat <<EOL >/home/ubuntu/docker-compose.yml
                    services:
                      java-app:
                        image: "642534338961.dkr.ecr.us-east-1.amazonaws.com/java-meven:latest"
                        ports:
                          - "8000:3306"
                    EOL
                    cd /home/ubuntu
                    sudo docker compose up -d
                    echo "Setup complete."
                 EOF
}

variable "key_name" {
  description = "The name of the SSH key pair"
  type        = string
}

variable "network_security_group_name" {
  description = "The name of the network security group"
  type        = string
}

variable "ubuntu_ami" {
  description = "The AMI ID for the Ubuntu image"
  type        = string
}

variable "ubuntu_instance_type" {
  description = "The EC2 instance type"
  type        = string
}
```
### `providers.tf`
```
Configure the AWS Provider
provider "aws" {
region = "us-east-1"
}
```
### Terraform Script Explanation

- **Provider Configuration**:
  - Specifies the AWS provider and its region.
    - The `provider` block in `main.tf` defines the AWS provider and sets the region to "us-east-1". This tells Terraform which cloud provider to use and in which region to provision resources.

- **IAM Role Definition**:
  - Defines an IAM role for EC2 instances.
    - The `aws_iam_role` resource in `main.tf` creates an IAM role named "ec2-role". IAM roles are entities that define permissions for making AWS service requests.
  - Allows EC2 instances to assume the role for access to AWS resources.
    - The `assume_role_policy` attribute specifies the permissions for assuming the role. In this case, it allows EC2 instances to assume the role, granting them access to the AWS services defined in the policy.
  - Defines policies for Amazon ECR and CloudWatch Logs.
    - The `aws_iam_role_policy` resource attaches a policy to the IAM role. Policies define permissions for actions in AWS. Here, policies allow EC2 instances to interact with Amazon ECR (Elastic Container Registry) and CloudWatch Logs.

- **Instance Profile**:
  - Associates the IAM role with an EC2 instance profile.
    - The `aws_iam_instance_profile` resource creates an instance profile named "ec2-instance-profile". Instance profiles are containers for IAM roles that can be associated with EC2 instances.
  - This ensures that the IAM role created earlier can be used by EC2 instances launched with this profile.

- **Key Pair Creation**:
  - Creates an SSH key pair for EC2 instances.
    - The `aws_key_pair` resource generates an SSH key pair named "deployer". Key pairs allow SSH access to EC2 instances, providing a secure way to connect to the instances remotely.

- **Security Group**:
  - Defines a network security group for EC2 instances.
    - The `aws_security_group` resource creates a security group named "network_security_group". Security groups act as virtual firewalls that control inbound and outbound traffic for EC2 instances.
  - Allows inbound SSH traffic.
    - The `ingress` block specifies rules to allow SSH traffic from any IP address. This allows SSH connections to the EC2 instances from any location.

- **EC2 Instance Creation**:
  - Launches an EC2 instance with specified configurations.
    - The `aws_instance` resource creates an EC2 instance named "ubuntu_vm_instance" using the specified AMI and instance type. This is the main resource that represents the EC2 instance.
  - Installs Docker and AWS CLI.
    - The `user_data` block contains a bash script that runs when the EC2 instance is launched. This script installs Docker and AWS CLI on the EC2 instance.
      - Updates the package manager and installs necessary dependencies.
      - Installs Docker and Docker Compose.
      - Installs AWS CLI.
      - Logs into Amazon ECR and pulls the latest Docker image.
      - Creates and runs a Docker container using the pulled image.
        
### Terraform Pipeline Script 

```groovy
pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/NEELOFARKHAN22/CI-CD-Complete-Pipeline-with-Jenkins.git'
            }
        }
        stage('Terraform init') {
            steps {
                sh 'echo $AWS_ACCESS_KEY_ID'
                sh 'terraform init'
            }
        }
        
        stage('Terraform validate') {
            steps {
                sh 'terraform validate'
            }
        }
        
        stage('Terraform plan') {
            steps {
                sh 'terraform plan -lock=false'
            }
        }
    
        stage('Terraform apply') {
            steps {
                sh 'terraform apply --auto-approve -lock=false'
            }
        }
        
    }
}
```
### Terraform Pipeline Script Explanation

The provided Jenkins pipeline script automates the execution of Terraform commands for infrastructure provisioning. Here's a breakdown of each stage in the script:

| Stage                   | Description                                                      |
|-------------------------|------------------------------------------------------------------|
| Checkout                | Clones the code repository from GitHub.                          |
| Terraform init          | Initializes Terraform within the Jenkins pipeline.               |
| Terraform validate      | Validates the Terraform configuration files.                     |
| Terraform plan          | Creates an execution plan for applying the Terraform configuration.|
| Terraform apply         | Applies the Terraform configuration to provision or update infrastructure.|

This script streamlines the process of infrastructure provisioning using Terraform within a Jenkins CI/CD pipeline.
These configurations enable the automated setup of AWS infrastructure using Terraform, allowing you to define your infrastructure as code.

### **Commit and Push Changes**
   - Once the Terraform configuration files are ready, commit and push them to your version control repository (e.g., Git).

### **Execute Jenkins Job**
   - Run the Jenkins job that integrates Terraform.
   - Jenkins will execute the Terraform commands defined in the job configuration.
   - Monitor the job output for any errors or warnings during Terraform execution.


