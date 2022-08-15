## Image 서버 Dockerfile

FROM openjdk:11

LABEL service="alpha-api" jdk-ver="11"

RUN mkdir -p /alpha
#RUN apk update
## openjdk 8 tz 설정.

##RUN
RUN apt update

RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
ENV TZ Asia/Seoul

RUN apt install -y tzdata


RUN groupadd -g 998 alpha
RUN useradd -r -u 998 -g alpha alpha

ARG JAR_FILE=target/*.jar

## jenkins workspace jar 파일 이동.
ADD ${JAR_FILE} /alpha/alpha-api.jar

## CMD, ENTRYPOINT 실행 경로.
WORKDIR /alpha

## profiles 설정 버전
## ENTRYPOINT ["javar", "-jar", "-Dspring.profiles.active=dev", "alpha-api-0.0.1-SNAPSHOT.jar"]
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","alpha-api.jar"]
