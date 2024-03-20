FROM maven:3.9.6 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17
COPY --from=build /target/slate-chatbox-0.0.1-SNAPSHOT.jar slate-chatbox.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "slate-chatbox.jar"]