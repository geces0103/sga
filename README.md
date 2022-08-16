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
            - aws configure set aws_access_key_id "AKIAZWWD4RGOLM7VF2CU"
            - aws configure set aws_secret_access_key "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"
            - eval $(aws ecr get-login --no-include-email --region us-east-1 | sed 's;https://;;g')
	-./gradlew build -x test
            - docker build -f src/main/java/br/com/sgi/config/docker/Dockerfile -t 667204487580.dkr.ecr.us-east-1.amazonaws.com/sgi:0.0.2 .
            - docker push 667204487580.dkr.ecr.us-east-1.amazonaws.com/sgi:0.0.1-SNAPSHOT
	docker pull 667204487580.dkr.ecr.us-east-1.amazonaws.com/sgi:0.0.1-SNAPSHOT

docker run -d -p 8080:9090 -e "SPRING_PROFILES_ACTIVE=test" 


FRONT END


		aws configure set aws_access_key_id "AKIAZWWD4RGOLM7VF2CU"
            - aws configure set aws_secret_access_key "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"
            - eval $(aws ecr get-login --no-include-email --region us-east-1 | sed 's;https://;;g')
	-npm install 
            - docker build -t 667204487580.dkr.ecr.us-east-1.amazonaws.com/sgi-fe:0.1.0 .
            - docker push 667204487580.dkr.ecr.us-east-1.amazonaws.com/sgi-fe:0.1.0
	docker pull 667204487580.dkr.ecr.us-east-1.amazonaws.com/sgi-fe:0.1.0


