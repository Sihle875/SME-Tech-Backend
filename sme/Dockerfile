FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN apt-get update && apt-get install -y maven

# Force Maven to run where pom.xml is
WORKDIR /app

RUN mvn -f pom.xml clean package -DskipTests

CMD ["java", "-jar", "target/*.jar"]
