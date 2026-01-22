# ---- Build stage ----
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# pom.xml önce (cache için)
COPY pom.xml .
RUN mvn dependency:go-offline

# kaynak kod
COPY src ./src

# jar üret
RUN mvn clean package -DskipTests


# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
