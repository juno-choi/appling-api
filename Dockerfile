FROM openjdk:17.0.2-slim

# jar파일 복사
COPY build/libs/appling-1.0.jar appling.jar
ENTRYPOINT ["java","-jar","appling.jar"]