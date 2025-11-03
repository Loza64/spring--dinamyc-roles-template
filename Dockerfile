# image
FROM eclipse-temurin:21-jdk

# directorio
WORKDIR /app

# copiar .jar
COPY target/*.jar app.jar

# puerto
EXPOSE 8080

# comando
ENTRYPOINT ["java", "-jar", "app.jar"]