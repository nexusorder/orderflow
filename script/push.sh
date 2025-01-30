#!/bin/sh
OWNER="FIXME"

if [ -n "$1" ] && [ -n "$2" ]; then
  DOCKER_ID="$1"
  DOCKER_PASSWORD="$2"
else
  while IFS=':' read -r id pw; do
    DOCKER_ID="$id"
    DOCKER_PASSWORD="$pw"
  done < ".dockerpassword"
fi

podman login docker.io -u "$DOCKER_ID" -p "$DOCKER_PASSWORD"

podman tag orderflow:0.0.1-amd64 docker.io/${OWNER}/orderflow:0.0.1-amd64
podman push docker.io/${OWNER}/orderflow:0.0.1-amd64
