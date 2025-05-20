FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Установка Maven вручную
RUN apt-get update && apt-get install -y maven

# Копируем весь контекст сборки
COPY . .

# Вывод списка файлов для отладки
RUN echo ">>> Список файлов после COPY:" && ls -l

# Проверяем версию Maven и Java
RUN mvn --version
RUN java -version

# Собираем приложение с использованием Maven в batch-режиме, пропуская тесты
RUN mvn -B clean package -DskipTests -e

# Вывод содержимого каталога target, чтобы убедиться, что сборка прошла успешно и артефакт создан
RUN echo ">>> Содержимое target:" && ls -l /app/target

FROM eclipse-temurin:17-jre
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=builder /app/target/demo11-1.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]