FROM eclipse-temurin:17-jre

WORKDIR /app

ARG JAR_FILE=target/smartmarket-backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]