# --- Build ---
FROM gradle:8.7.0-jdk21 AS build

WORKDIR /RouteMoodServer
COPY . .

# Installing dependencies
USER root
RUN apt-get update && \
    apt-get install -y python3-pip && \
    pip3 install jinja2-cli && \
    apt-get clean

RUN chmod +x generate-config.sh
RUN ./generate-config.sh
RUN ./gradlew build -x test

# --- Run ---
FROM eclipse-temurin:21-jre
WORKDIR /RouteMoodServer
COPY --from=build /RouteMoodServer/app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["sh", "-c", "echo \"${GPT_TOKEN}\" > token.txt && java -jar app.jar token.txt"]