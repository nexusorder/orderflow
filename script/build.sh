#!/bin/sh
../orderflow/gradlew clean build -x test -p ../orderflow
npm install --prefix ../frontend
npm run build --prefix ../frontend
podman build --platform linux/amd64  -t orderflow:0.0.1-amd64 -f ../docker/Dockerfile ../
