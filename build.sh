#!/bin/bash

echo "Building Farm Stock Management Backend..."

cd backend
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "JAR file created at: backend/target/"
else
    echo "Build failed!"
    exit 1
fi 