FROM gradle:jdk19 as build
COPY . /app
WORKDIR /app
RUN gradle clean build

FROM openjdk:20-ea-jdk-slim-buster
COPY --from=build /app/build/libs/ResourceRadar-API-0.0.1-SNAPSHOT.jar /app/ 
WORKDIR /app
ENTRYPOINT ["java", "-jar","ResourceRadar-API-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080

