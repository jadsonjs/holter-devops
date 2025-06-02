

#
# docker build . -t jadsonjs/holter-devops:v1-imd --build-arg ( FRONTEND_BUILD=build:dev or FRONTEND_BUILD=build:test or FRONTEND_BUILD=build:prod)
#
# docker login
# docker push jadsonjs/holter-devops:v1-imd
#
FROM gradle:8.12.1-jdk21 as builder
ARG FRONTEND_BUILD
WORKDIR /app

# copy all files to current directory "app"
COPY . .

# FRONTEND_BUILD = build:test or build:prod
RUN echo ">>> building frontend inside dockerfile with parameter: $FRONTEND_BUILD"
RUN gradle clean build -Pfrontend_build=$FRONTEND_BUILD

#
#
#
# Ex:
# docker container run -d -p 8200:8080 --env DATABASE_PATH=/data --env DATABASE_PASSWORD=xxxxxx --env MAIL_ACCOUNT=mail@company.br --env MAIL_PASSWORD='xxxx xxxx xxxx xxxx' --env TOKEN_SECRET=xxxxxxxxxx --env APPLICATION_URL=https://service.compony.br/holter -v /data/holter:/data --name holter-devops jadsonjs/holter-devops:v1-imd
#
# bind /data/holter directory to /data inside the docker and user the docker directory "/data" as an env variable
#
# h2-console: jdbc:h2:/data/holter_db (inside the docker)
#
FROM eclipse-temurin:21-jre-alpine

# install curl and download grype
RUN apk add --no-cache bash curl
RUN curl -sSfL https://github.com/anchore/grype/releases/download/v0.90.0/grype_0.90.0_linux_amd64.tar.gz | tar xz -C /usr/local/bin grype
RUN grype db update
# ensure Grype scan is run even when DB is outdated
ENV GRYPE_DB_VALIDATE_AGE=false

# download trivy
RUN curl -sSL https://github.com/aquasecurity/trivy/releases/download/v0.61.0/trivy_0.61.0_Linux-64bit.tar.gz | tar -xz -C /usr/local/bin trivy
RUN trivy image --download-db-only

ARG APP_NAME=holter

# set locale in Docker Alpine
RUN apk update
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/America/Recife /etc/localtime
RUN echo "America/Recife" >  /etc/timezone

ENV TZ America/Recife
ENV LANG pt_BR.UTF-8
ENV LANGUAGE pt_BR.UTF-8
ENV LC_ALL pt_BR.UTF-8


COPY --from=builder app/build/libs/$APP_NAME*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

