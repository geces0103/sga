# Projeto Arquitetural PUC Minas
# Implementação API back end java 11, com Spring boot cadastro de Usuários
# Gradle
# Swagger
# Integração com Keycloak

# O contexto da aplicação sobe no /sgi na porta 9090
# Dockerfile na pasta config do projeto
# Container publicado na AWS


Docker steps script

 - pip3 install awscli
            - aws configure set aws_access_key_id "xxxxxx"
            - aws configure set aws_secret_access_key "xxxxxx"
            - eval $(aws ecr get-login --no-include-email --region us-east-1 | sed 's;https://;;g')
	-./gradlew build -x test
            - docker build -f src/main/java/br/com/sgi/config/docker/Dockerfile -t xxxxxx/sgi:0.0.2 .
            - docker push xxxxxx/sgi:0.0.1-SNAPSHOT
	docker pull xxxxxx/sgi:0.0.1-SNAPSHOT

docker run -d -p 8080:9090 -e "SPRING_PROFILES_ACTIVE=test" 


FRONT END


		aws configure set aws_access_key_id "xxxxx"
            - aws configure set aws_secret_access_key "xxxxxx"
            - eval $(aws ecr get-login --no-include-email --region us-east-1 | sed 's;https://;;g')
	-npm install 
            - docker build -t xxxxxx/sgi-fe:0.1.0 .
            - docker push xxxxxx/sgi-fe:0.1.0
	docker pull xxxxxx/sgi-fe:0.1.0


