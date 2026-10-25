# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Download dependencies first so they are cached between builds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/target/*.jar app.jar
COPY uploads/avatars/default-avatar.png uploads/avatars/default-avatar.png
RUN mkdir -p uploads/images uploads/videos

# Low-memory JVM settings for small containers (override with JAVA_OPTS)
ENV JAVA_OPTS="-Xmx256m -Xss512k -XX:MaxMetaspaceSize=150m -XX:+UseSerialGC -XX:TieredStopAtLevel=1"
ENV PORT=8080

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
