FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . /app/
RUN ./mvnw -DskipTests clean package
COPY target/demo11.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]