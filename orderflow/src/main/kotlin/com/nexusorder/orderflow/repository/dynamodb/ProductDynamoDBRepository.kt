package com.nexusorder.orderflow.repository.dynamodb

import com.nexusorder.orderflow.model.storage.Product
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.ProjectionType

@Repository
@Profile("!test")
class ProductDynamoDBRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) : AbstractDynamoDBRepository<Product>(
    dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient,
    tableName = "product",
    clazz = Product::class.java
) {

    fun findAllByShopId(shopId: String): Flux<Product> {
        return findAllWithIndex("shopId-index", shopId)
    }

    fun createTable(): Mono<Boolean> {
        val shopIdGsi = EnhancedGlobalSecondaryIndex.builder()
            .indexName("shopId-index")
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
        return super.createTable(listOf(shopIdGsi, categoryGsi))
    }
}
