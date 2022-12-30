FROM openjdk:11
ARG JAR_FULE=build/libs/app.jar
COPY ${JAR_FULE} ./app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "./app.jar"]