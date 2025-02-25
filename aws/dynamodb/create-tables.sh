#!/bin/sh
aws dynamodb create-table \
    --table-name category \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=key,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --global-secondary-indexes \
    "[
        {
            \"IndexName\": \"key-index\",
            \"KeySchema\": [
                {\"AttributeName\": \"key\", \"KeyType\": \"HASH\"}
            ],
            \"Projection\": {\"ProjectionType\": \"ALL\"},
            \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 1, \"WriteCapacityUnits\": 1}
        }
    ]" \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --table-class STANDARD

aws dynamodb create-table \
    --table-name member \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=login,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --global-secondary-indexes \
    "[
        {
            \"IndexName\": \"login-index\",
            \"KeySchema\": [
                {\"AttributeName\": \"login\", \"KeyType\": \"HASH\"}
            ],
            \"Projection\": {\"ProjectionType\": \"ALL\"},
            \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 1, \"WriteCapacityUnits\": 1}
        }
    ]" \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --table-class STANDARD

aws dynamodb create-table \
    --table-name order \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=memberId,AttributeType=S \
        AttributeName=shopId,AttributeType=S \
        AttributeName=reviewId,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --global-secondary-indexes \
    "[
        {
            \"IndexName\": \"memberId-index\",
            \"KeySchema\": [
                {\"AttributeName\": \"memberId\", \"KeyType\": \"HASH\"}
            ],
            \"Projection\": {\"ProjectionType\": \"ALL\"},
            \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 1, \"WriteCapacityUnits\": 1}
        },
        {
            \"IndexName\": \"shopId-index\",
            \"KeySchema\": [
                {\"AttributeName\": \"shopId\", \"KeyType\": \"HASH\"}
            ],
            \"Projection\": {\"ProjectionType\": \"ALL\"},
            \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 1, \"WriteCapacityUnits\": 1}
        },
        {
            \"IndexName\": \"reviewId-index\",
            \"KeySchema\": [
                {\"AttributeName\": \"reviewId\", \"KeyType\": \"HASH\"}
            ],
            \"Projection\": {\"ProjectionType\": \"ALL\"},
            \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 1, \"WriteCapacityUnits\": 1}
        }
    ]" \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --table-class STANDARD

aws dynamodb create-table \
    --table-name product \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=shopId,AttributeType=S \
        AttributeName=category,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --global-secondary-indexes \
    "[
        {
            \"IndexName\": \"shopId-index\",
            \"KeySchema\": [
                {\"AttributeName\": \"shopId\", \"KeyType\": \"HASH\"}
            ],
            \"Projection\": {\"ProjectionType\": \"ALL\"},
            \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 1, \"WriteCapacityUnits\": 1}
        },
        {
            \"IndexName\": \"category-index\",
            \"KeySchema\": [
                {\"AttributeName\": \"category\", \"KeyType\": \"HASH\"}
            ],
            \"Projection\": {\"ProjectionType\": \"ALL\"},
            \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 1, \"WriteCapacityUnits\": 1}
        }
    ]" \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --table-class STANDARD

aws dynamodb create-table \
    --table-name shop \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=owner,AttributeType=S \
        AttributeName=category,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --global-secondary-indexes \
    "[
        {
            \"IndexName\": \"owner-index\",
            \"KeySchema\": [
                {\"AttributeName\": \"owner\", \"KeyType\": \"HASH\"}
            ],
            \"Projection\": {\"ProjectionType\": \"ALL\"},
            \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 1, \"WriteCapacityUnits\": 1}
        },
        {
            \"IndexName\": \"category-index\",
            \"KeySchema\": [
                {\"AttributeName\": \"category\", \"KeyType\": \"HASH\"}
            ],
            \"Projection\": {\"ProjectionType\": \"ALL\"},
            \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 1, \"WriteCapacityUnits\": 1}
        }
    ]" \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --table-class STANDARD
