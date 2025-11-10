FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY config-server/pom.xml .
RUN mvn dependency:go-offline -B || true
COPY config-server/src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8888
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8888/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
