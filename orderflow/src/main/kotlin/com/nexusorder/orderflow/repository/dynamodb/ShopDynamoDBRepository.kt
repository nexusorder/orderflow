package com.nexusorder.orderflow.repository.dynamodb

import com.nexusorder.orderflow.model.storage.Shop
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.ProjectionType

@Repository
@Profile("!test")
class ShopDynamoDBRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) : AbstractDynamoDBRepository<Shop>(
    dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient,
    tableName = "shop",
    clazz = Shop::class.java
) {

    fun createTable(): Mono<Boolean> {
        val ownerGsi = EnhancedGlobalSecondaryIndex.builder()
            .indexName("owner-index")
            .projection { it.projectionType(ProjectionType.ALL) }
            .provisionedThroughput {
                it.readCapacityUnits(1L).writeCapacityUnits(1L)
            }
            .build()

        val categoryGsi = EnhancedGlobalSecondaryIndex.builder()
            .indexName("category-index")
            .projection { it.projectionType(ProjectionType.ALL) }
            .provisionedThroughput {
                it.readCapacityUnits(1L).writeCapacityUnits(1L)
            }
            .build()
        return super.createTable(listOf(ownerGsi, categoryGsi))
    }
}
