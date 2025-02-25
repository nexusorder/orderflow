package com.nexusorder.orderflow.repository.dynamodb

import com.nexusorder.orderflow.model.storage.Review
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient

@Repository
@Profile("!test")
class ReviewDynamoDBRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) : AbstractDynamoDBRepository<Review>(
    dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient,
    tableName = "review",
    clazz = Review::class.java
)
