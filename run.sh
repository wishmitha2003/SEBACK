#!/bin/bash
echo "Starting EzyEnglish Authentication Service..."
echo "Building the project with Maven..."
mvn clean install -DskipTests
if [ $? -eq 0 ]; then
    echo "Build successful! Starting the application on port 8082..."
    mvn spring-boot:run
else
    echo "Build failed. Please check the errors above."
    exit 1
fi
