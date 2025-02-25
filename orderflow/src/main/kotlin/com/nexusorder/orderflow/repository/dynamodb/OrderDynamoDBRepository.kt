package com.nexusorder.orderflow.repository.dynamodb

import com.nexusorder.orderflow.model.storage.Order
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.ProjectionType

@Repository
@Profile("!test")
class OrderDynamoDBRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) : AbstractDynamoDBRepository<Order>(
    dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient,
    tableName = "order",
    clazz = Order::class.java
) {

    fun findAllByMemberId(memberId: String): Flux<Order> {
        return findAllWithIndex("memberId-index", memberId)
    }

    fun createTable(): Mono<Boolean> {
        val memberIdGsi = EnhancedGlobalSecondaryIndex.builder()
            .indexName("memberId-index")
            .projection { it.projectionType(ProjectionType.ALL) }
            .provisionedThroughput {
                it.readCapacityUnits(1L).writeCapacityUnits(1L)
            }
            .build()

        val shopIdGsi = EnhancedGlobalSecondaryIndex.builder()
            .indexName("shopId-index")
            .projection { it.projectionType(ProjectionType.ALL) }
            .provisionedThroughput {
                it.readCapacityUnits(1L).writeCapacityUnits(1L)
            }
            .build()

        val reviewIdGsi = EnhancedGlobalSecondaryIndex.builder()
            .indexName("reviewId-index")
            .projection { it.projectionType(ProjectionType.ALL) }
            .provisionedThroughput {
                it.readCapacityUnits(1L).writeCapacityUnits(1L)
            }
            .build()
        return super.createTable(listOf(memberIdGsi, shopIdGsi, reviewIdGsi))
    }
}
