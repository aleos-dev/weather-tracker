# Build *.war
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Grab Tomcat
FROM tomcat:10.1.30-jdk21 AS deploy
RUN addgroup app-group \
    && adduser --ingroup app-group app-user \
    && chown -R app-user:app-group /usr/local/tomcat
USER app-user:app-group

WORKDIR /usr/local/tomcat/webapps/
ARG WAR_FILENAME=weather-tracker.war
COPY --from=build /app/target/${WAR_FILENAME} ROOT.war

EXPOSE 8080

ENTRYPOINT ["catalina.sh"]
CMD ["run"]
