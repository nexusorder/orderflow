#!/bin/sh
docker pull amazon/dynamodb-local:2.5.4
docker run -p 8000:8000 amazon/dynamodb-local
