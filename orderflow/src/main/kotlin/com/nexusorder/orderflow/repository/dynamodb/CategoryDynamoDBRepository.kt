package com.nexusorder.orderflow.repository.dynamodb

import com.nexusorder.orderflow.model.storage.Category
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.ProjectionType

@Repository
@Profile("!test")
class CategoryDynamoDBRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
) : AbstractDynamoDBRepository<Category>(
    dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient,
    tableName = "category",
    clazz = Category::class.java
) {

    fun findByKey(key: String): Mono<Category> {
        return findWithIndex("key-index", key)
    }

    fun createTable(): Mono<Boolean> {
        val keyIndexGsi = EnhancedGlobalSecondaryIndex.builder()
            .indexName("key-index")
            .projection { it.projectionType(ProjectionType.ALL) }
            .provisionedThroughput {
                it.readCapacityUnits(1L).writeCapacityUnits(1L)
            }
            .build()
        return super.createTable(listOf(keyIndexGsi))
    }
}
