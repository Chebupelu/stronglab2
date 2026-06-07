# Этап 1: Сборка приложения
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем файлы с зависимостями для кэширования слоёв Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем .jar файл
COPY src ./src
RUN mvn clean package -DskipTests -B

# Этап 2: Запуск приложения
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Копируем собранный .jar файл из первого этапа
COPY --from=build /app/target/*.jar app.jar

# Указываем порт, который будет слушать приложение
EXPOSE 8081

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]