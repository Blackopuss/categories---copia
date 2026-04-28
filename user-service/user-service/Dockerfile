# Etapa de construcción (Build stage)
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copia los scripts y archivos de Maven
COPY .mvn/ .mvn/
COPY mvnw ./
COPY pom.xml ./

# Asegurarse de que el script mvnw tiene permisos de ejecución
RUN chmod +x ./mvnw

# Descarga las dependencias
RUN ./mvnw dependency:go-offline -B

# Copia el código fuente y construye el empaquetado
COPY src/ ./src/
RUN ./mvnw clean package -DskipTests

# Etapa de ejecución (Run stage)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia únicamente el jar ya construido
COPY --from=builder /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar

# Define el punto de entrada para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]