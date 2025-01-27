# Backend와 Frontend를 합친 Dockerfile입니다.
FROM --platform=linux/amd64 eclipse-temurin:17.0.13_11-jre-ubi9-minimal as backend
RUN mkdir -p /app
WORKDIR /app
COPY orderflow/build/libs/orderflow-0.0.1-SNAPSHOT.jar app.jar

# copy frontend
COPY frontend/build /web/

ENTRYPOINT ["java","-jar","./app.jar"]
