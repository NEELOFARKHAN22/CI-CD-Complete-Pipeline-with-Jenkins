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
                dir('/var/lib/jenkins/workspace/CI-CD-Pipeline/f1') {
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
            sh 'docker build -t java-maven .'                             
        }

        stage('Push to ECR') {
            steps {
                script {
                        sh 'aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 642534338961.dkr.ecr.us-east-1.amazonaws.com'
                        sh 'docker tag java-meven:latest 642534338961.dkr.ecr.us-east-1.amazonaws.com/java-meven:latest'
                        sh 'docker push 642534338961.dkr.ecr.us-east-1.amazonaws.com/java-meven:latest'
                       
                }
            }
        }
        
        stage('Checkout-for-terraform') {
            steps {
                git 'https://github.com/NEELOFARKHAN22/CI-CD-Complete-Pipeline-with-Jenkins.git'
            }
        }
        stage('Terraform workflow') {
            steps {
                dir('/var/lib/jenkins/workspace/CI-CD-Pipeline/Terraform') {
                    script{
                        sh 'echo $AWS_ACCESS_KEY_ID'
                        sh 'terraform init'
                        sh 'terraform validate'
                        sh 'terraform plan -lock=false'
                        sh 'terraform apply --auto-approve -lock=false'
                    }
                }
            }
            
            
        }
      
    }

}
