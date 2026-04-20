# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper first so dependency downloads are cached as a separate layer.
# This layer is only invalidated when pom.xml changes, not on every source change.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Fix Windows CRLF line endings (common when the repo is checked out on Windows)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Pre-download all dependencies (cached layer — skipped on subsequent builds
# unless pom.xml changes)
RUN ./mvnw dependency:go-offline -q

# Copy source and build the fat JAR, skipping tests (tests run in CI)
COPY src ./src
RUN ./mvnw package -DskipTests -q

# ─── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Run as a non-root user
RUN apk add --no-cache wget && \
    addgroup -S wasel && adduser -S wasel -G wasel

COPY --from=builder /app/target/wasel-0.0.1-SNAPSHOT.jar app.jar

RUN chown wasel:wasel app.jar
USER wasel

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
