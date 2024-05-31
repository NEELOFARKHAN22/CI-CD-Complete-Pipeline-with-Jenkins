# Creating key-pair on AWS using SSH-public key
resource "aws_key_pair" "deployer" {
  key_name   = var.key-name
  public_key = file("${path.module}/my-key.pub")
}

# Creating a security group to restrict/allow inbound connectivity
resource "aws_security_group" "network-security-group" {
  name        = var.network-security-group-name
  description = "Allow TLS inbound traffic"

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] 
  }
  # Not recommended to add "0.0.0.0/0" instead we need to be more specific with the IP ranges to allow connectivity from.
  tags = {
    Name = "nsg-inbound"
  }
}


# Creating Ubuntu EC2 instance
resource "aws_instance" "ubuntu-vm-instance" {
  ami             = var.ubuntu-ami
  instance_type   = var.ubuntu-instance-type
  key_name        = aws_key_pair.deployer.key_name
  vpc_security_group_ids = [aws_security_group.network-security-group.id]
  tags = {
    Name = "ubuntu-vm"
  }
  user_data = <<-EOF
                    #!/bin/bash
                    sudo apt update
                    sudo apt install ca-certificates curl
                    sudo install -m 0755 -d /etc/apt/keyrings
                    sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
                    sudo chmod a+r /etc/apt/keyrings/docker.asc
                    echo \
                      "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
                       $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
                        sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
                    sudo apt update
                    sudo apt install docker-compose-plugin -y
                    docker compose version
                    sudo apt-get install unzip
                    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                    unzip awscliv2.zip
                    sudo ./aws/install
                    sudo snap install aws-cli
                    aws ecr get-login-password --region us-east-1 && docker login --username AWS --password-stdin 642534338961.dkr.ecr.us-east-1.amazonaws.com
                    docker pull 642534338961.dkr.ecr.us-east-1.amazonaws.com/java-meven:latest
                    #docker run -it --name container1 java-meven:latest
                    cat <<EOL >docker-compose.yml
                    version: '3'
                      services:
                        java-app:
                          image: "642534338961.dkr.ecr.us-east-1.amazonaws.com/java-meven:latest"
                          ports:
                            - "8000:3306"
                    EOL
                    docker-compose up -d
                 EOF


}
