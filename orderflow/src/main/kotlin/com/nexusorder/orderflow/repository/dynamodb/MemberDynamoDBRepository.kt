package com.nexusorder.orderflow.repository.dynamodb

import com.nexusorder.orderflow.model.storage.Member
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.ProjectionType

@Repository
@Profile("!test")
class MemberDynamoDBRepository(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
) : AbstractDynamoDBRepository<Member>(
    dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient,
    tableName = "member",
    clazz = Member::class.java
) {

    fun findByLogin(login: String): Mono<Member> {
        return findWithIndex("login-index", login)
    }

    fun existsByLogin(login: String): Mono<Boolean> {
        return findByLogin(login)
            .map { true }
            .switchIfEmpty(Mono.just(false))
    }

    fun createTable(): Mono<Boolean> {
        val loginGsi = EnhancedGlobalSecondaryIndex.builder()
            .indexName("login-index")
            .projection { it.projectionType(ProjectionType.ALL) }
            .provisionedThroughput {
                it.readCapacityUnits(1L).writeCapacityUnits(1L)
            }
            .build()
        return super.createTable(listOf(loginGsi))
    }
}
