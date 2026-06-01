FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
ARG QUARKUS_PROFILE=oracle
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests -Dquarkus.profile=${QUARKUS_PROFILE} package

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
ENV QUARKUS_PROFILE=oracle
COPY --from=build /workspace/target/quarkus-app/lib/ ./lib/
COPY --from=build /workspace/target/quarkus-app/*.jar ./
COPY --from=build /workspace/target/quarkus-app/app/ ./app/
COPY --from=build /workspace/target/quarkus-app/quarkus/ ./quarkus/
EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -Dquarkus.profile=${QUARKUS_PROFILE} -jar quarkus-run.jar"]
