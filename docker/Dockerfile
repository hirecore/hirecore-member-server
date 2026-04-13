FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN groupadd --system spring \
    && useradd --system --gid spring --create-home spring

COPY build/libs/*.jar /app/app.jar

RUN chown spring:spring /app/app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Duser.timezone=UTC", "-jar", "/app/app.jar"]