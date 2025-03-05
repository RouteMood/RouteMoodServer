./gradlew build -x test
cp build/libs/AuthService-0.0.1-SNAPSHOT.jar .
docker-compose down
docker image rm auth-service:latest
docker-compose up
