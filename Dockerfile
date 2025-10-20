FROM openjdk:17-jdk-slim
COPY . .
CMD ["./mvnw", "spring-boot:run"]   ,0