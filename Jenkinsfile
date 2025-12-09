pipeline {
    agent any

    stages {

        stage('Récupération du projet') {
            steps {
                echo 'Clonage du dépôt Git public...'
                git branch: 'main', url: 'https://github.com/MaisseneJradi/projet_devops.git'
            }
        }

        stage('Maven') {
            steps {
                echo 'Compilation avec Maven...'
                sh 'mvn compile'
            }
        }

        stage('Tests unitaires') {
            steps {
                echo 'Exécution des tests unitaires...'
                sh 'mvn test'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                echo 'Analyse de la qualité du code avec SonarQube...'
                sh '''
                    mvn sonar:sonar \
                        -Dsonar.projectKey=eventsProject \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.login=squ_6ed9ccff38c1ba7cdfe56ef25c3cafedfe5afd18
                '''
            }
        }

        stage('Préparation de la version') {
            steps {
                echo 'Préparation de la version à distribuer...'
                sh 'mvn install'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-deployment-credentials',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {

                    sh """
                        mvn clean deploy -DskipTests=true \
                        -DaltDeploymentRepository=deploymentRepo::default::http://${NEXUS_USER}:${NEXUS_PASS}@localhost:8081/repository/maven-snapshots/
                    """
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t eventsproject:latest .'

                    sh 'docker tag eventsproject:latest localhost:8082/eventsproject:latest'
                    sh "docker tag eventsproject:latest localhost:8082/eventsproject:${BUILD_NUMBER}"

                    sh 'docker images'
                }
            }
        }

        stage('Push Docker Image → Nexus Registry') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-deployment-credentials',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {

                    sh '''
                        echo "$NEXUS_PASS" | docker login -u "$NEXUS_USER" --password-stdin localhost:8082
                        docker push localhost:8082/eventsproject:latest
                        docker push localhost:8082/eventsproject:${BUILD_NUMBER}
                    '''
                }
            }
        }

        stage('Push Docker Image to DockerHub') {
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    retry(3) {
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-conn',
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )]) {

                            sh '''
                                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                                docker push maissene123/eventsproject:latest
                                docker push maissene123/eventsproject:${BUILD_NUMBER}
                            '''
                        }
                    }
                }
            }
        }

        stage('Deploy with docker-compose') {
            steps {
                sh '''
                    docker compose down || true
                    docker compose up -d
                    sleep 10
                    docker ps
                '''
            }
        }

    } // end stages
} // end pipeline
