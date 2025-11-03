# image
FROM eclipse-temurin:21-jdk

# directorio
WORKDIR /app

# copiar .jar
COPY target/*.jar app.jar

# puerto
EXPOSE 4000

# comando
ENTRYPOINT ["java", "-jar", "app.jar"]