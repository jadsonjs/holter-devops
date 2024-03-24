

#
# docker build . -t jadsonjs/holter-devops:v1-org
#
# docker login
# docker push jadsonjs/holter-devops:v1-org
#
FROM gradle:7.6.2-jdk17 as builder
WORKDIR /app

# copy all files to current directory "app"
COPY . .

RUN gradle build

#
# docker container run -d -p 8XXX:8080 --env DATABASE_PATH=/data --env DATABASE_PASSWORD=xxxxxx --env MAIL_ACCOUNT=jadson.santos@ufrn.br --env MAIL_PASSWORD='xxxx xxxx xxxx xxxx' -v /local-machine-db-directory:/data --name holter-devops jadsonjs/holter-devops:v1
#
# Ex .:
# docker container run -d -p 8200:8080 --env DATABASE_PATH=/data --env DATABASE_PASSWORD=xxxxxx --env MAIL_ACCOUNT=jadson.santos@ufrn.br --env MAIL_PASSWORD='xxxx xxxx xxxx xxxx' --env TOKEN_SECRET=xxxxxxxxxx -v /data/holter:/data --name holter-devops jadsonjs/holter-devops:v1-org
#
# bind /data/holter directory to /data inside the docker and user the docker directory "/data" as an env variable
#
# h2-console: jdbc:h2:/data/holter_db (inside the docker)
#
FROM eclipse-temurin:21-jre-alpine
ARG APP_NAME=holter
ARG DATABASE_PATH

COPY --from=builder app/build/libs/$APP_NAME*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

