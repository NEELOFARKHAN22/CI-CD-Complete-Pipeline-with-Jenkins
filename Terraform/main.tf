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

# Create key pair for EC2
resource "aws_key_pair" "deployer" {
  key_name   = var.key_name
  public_key = file("${path.module}/my-key.pub")
}

# Create security group
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

# Create EC2 instance
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
