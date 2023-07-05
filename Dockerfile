FROM openjdk:11-slim
COPY ./build/libs/ecomtest-1.0.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
