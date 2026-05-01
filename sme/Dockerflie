# Use official Java image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the JAR file
CMD ["java", "-jar", "target/*.jar"]
