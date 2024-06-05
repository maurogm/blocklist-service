FROM sbtscala/scala-sbt:eclipse-temurin-alpine-17.0.10_7_1.10.0_2.13.14

WORKDIR /app

# Download dependencies first
COPY build.sbt .
COPY project /app/project
RUN sbt update

# Then copy rest the of the code and compile
COPY . .
RUN sbt clean compile

EXPOSE 8080

ENTRYPOINT ["sbt", "run"]
