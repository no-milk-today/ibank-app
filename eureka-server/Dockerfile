FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Копируем все файлы проекта, включая корневой pom.xml и дочерние модули
COPY . .

# Сборка всего проекта с пропуском тестов
RUN mvn clean package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем собранный jar конкретного микросервиса из target данной модуля
COPY --from=build /app/eureka-server/target/eureka-server-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
