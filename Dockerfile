# 자바 17 베이스 이미지 사용
FROM openjdk:17-jdk-oraclelinux7

LABEL maintainer="wjsalstjr59@gmail.com"

# 작업 디렉토리 설정
WORKDIR /app

#jar 파일 경로를 변수로 설정
ARG JAR_FILE=build/libs/AlertMessaging-0.0.1-SNAPSHOT.jar

#도커 이미지에 JAR_FILE을 복사
COPY ${JAR_FILE} AlertMessaging.jar

# 애플리케이션 실행을 위한 명령어 설정
CMD ["java", "-jar", "AlertMessaging.jar"]