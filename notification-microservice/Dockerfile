FROM eclipse-temurin:17-jdk

#INFORMA EL PUERTO DONDE SE EJECUTA EL CONTENEDOR
EXPOSE 8084

#DEFINIR DIRECTORIO RAIZ DEL CONTENEDOR
WORKDIR /app

COPY target/notification-microservice-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]