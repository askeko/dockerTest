FROM maven:3.9.0-amazoncorretto-17 as builder

COPY src /usr/src/app/src
COPY pom.xml /usr/src/app

RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:17-oracle
LABEL maintainer="Rune Ettrup <rse@netcompany.com>"
EXPOSE 8080
COPY --from=builder /usr/src/app/target/application.jar app.jar
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "/app.jar" \
]