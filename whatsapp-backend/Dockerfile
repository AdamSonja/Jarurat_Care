# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:22-jre

# Set the working directory
WORKDIR /app

# Copy the built jar to the container
COPY target/whatsapp-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port (Render will set $PORT)
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "app.jar"] 