#!/bin/sh
podman run --rm -d -p 8090:8080 --name orderflow orderflow:0.0.1
