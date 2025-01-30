package com.nexusorder.orderflow.repository.dynamodb

import com.nexusorder.orderflow.model.storage.AbstractCoreModel
import com.nexusorder.orderflow.util.CoreLogger
import org.reactivestreams.Publisher
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException

// 재사용을 위한 추상 클래스 DynamoDB 레포지토리
abstract class AbstractDynamoDBRepository<T : AbstractCoreModel>(
    dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    private val tableName: String,
    clazz: Class<T>
) : ReactiveCrudRepository<T, String> {

    // 주어진 테이블 이름으로 테이블 객체를 생성합니다.
    protected val table: DynamoDbAsyncTable<T> =
        dynamoDbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(clazz))

    // 조회
    override fun findById(id: String): Mono<T> {
        return Mono.fromFuture(
            table.getItem { builder ->
                // 기본 키(파티션 키)를 통해 조회합니다.
                builder.key { it.partitionValue(id) }
            }
        )
    }

    override fun findById(id: Publisher<String>): Mono<T> {
        return Mono.from(id)
            .flatMap { plainId ->
                Mono.fromFuture(
                    table.getItem { builder ->
                        builder.key { it.partitionValue(plainId) }
                    }
                )
            }
    }

    override fun findAll(): Flux<T> {
        return Flux.from(table.scan().items())
    }

    override fun findAllById(ids: MutableIterable<String>): Flux<T> {
        return Flux.fromIterable(ids)
            .flatMap { findById(it) }
    }

    override fun findAllById(idStream: Publisher<String>): Flux<T> {
        return Flux.from(idStream)
            .flatMap { findById(it) }
    }

    override fun existsById(id: String): Mono<Boolean> {
        return Mono.fromFuture(table.getItem { builder -> builder.key { it.partitionValue(id) } })
            .map { it != null }
    }

    override fun existsById(id: Publisher<String>): Mono<Boolean> {
        return Mono.from(id)
            .flatMap { id_str ->
                Mono.fromFuture(table.getItem { builder -> builder.key { it.partitionValue(id_str) } })
            }
            .map { it != null }
    }

    override fun count(): Mono<Long> {
        return Flux.from(table.scan().items())
            .count()
            .map { it.toLong() }
    }

    // 저장
    override fun <S : T> save(entity: S): Mono<S> {
        return Mono.fromFuture(table.putItem(entity))
            .thenReturn(entity)
    }

    override fun <S : T> saveAll(entities: MutableIterable<S>): Flux<S> {
        return Flux.fromIterable(entities)
            .flatMap { save(it) }
    }

    override fun <S : T> saveAll(entityStream: Publisher<S>): Flux<S> {
        return Flux.from(entityStream)
            .flatMap { save(it) }
    }

    // 삭제
    override fun delete(entity: T): Mono<Void> {
        return Mono.fromFuture(
            table.deleteItem {
                it.key {
                    // 기본 키(파티션 키)를 통해 삭제합니다.
                    it.partitionValue(entity.id)
                }
            }
        ).then()
    }

    override fun deleteById(id: String): Mono<Void> {
        return Mono.fromFuture(table.deleteItem { builder -> builder.key { it.partitionValue(id) } })
            .then()
    }

    override fun deleteById(id: Publisher<String>): Mono<Void> {
        return Mono.from(id)
            .flatMap { id_str ->
                Mono.fromFuture(table.deleteItem { builder -> builder.key { it.partitionValue(id_str) } })
            }
            .then()
    }

    override fun deleteAllById(ids: MutableIterable<String>): Mono<Void> {
        return Flux.fromIterable(ids)
            .flatMap { deleteById(it) }
            .then()
    }

    override fun deleteAll(entities: MutableIterable<T>): Mono<Void> {
        return Flux.fromIterable(entities)
            .flatMap { delete(it) }
            .then()
    }

    override fun deleteAll(entityStream: Publisher<out T>): Mono<Void> {
        return Flux.from(entityStream)
            .flatMap { delete(it) }
            .then()
    }

    override fun deleteAll(): Mono<Void> {
        return Mono.fromFuture(table.deleteTable())
            .then()
    }

    open fun findWithIndex(index: String, value: String): Mono<T> {
        return findAllWithIndex(index, value).next()
    }

    open fun findAllWithIndex(index: String, value: String): Flux<T> {
        val keyCondition = QueryConditional.keyEqualTo {
            it.partitionValue(value)
        }
        return Mono.from(
            table.index(index)
                .query(QueryEnhancedRequest.builder().queryConditional(keyCondition).build())
        ).flatMapMany { page: Page<T> ->
            if (page.items().isNotEmpty()) {
                Flux.fromIterable(page.items())
            } else {
                Flux.empty()
            }
        }.onErrorResume { ex ->
            when (ex) {
                is ResourceNotFoundException -> Flux.empty()
                else -> Flux.error(ex)
            }
        }
    }

    open fun createTable(gsiCollection: Collection<EnhancedGlobalSecondaryIndex> = listOf()): Mono<Boolean> {
        return Mono.fromFuture(
            table.createTable(
                CreateTableEnhancedRequest.builder()
                    .globalSecondaryIndices(gsiCollection)
                    .provisionedThroughput {
                        it.readCapacityUnits(1L).writeCapacityUnits(1L)
                    }.build()
            )
        ).map {
            CoreLogger.info("AbstractDynamoDBRepository", message = "Table created: $tableName")
            true
        }.onErrorResume {
            if (it is ResourceInUseException) {
                // table already exists
                CoreLogger.info("AbstractDynamoDBRepository", message = "Table already exists: $tableName")
                Mono.just(false)
            } else {
                Mono.error(it)
            }
        }
    }

    /*
    fun update(user: User): Mono<User> {
        return Mono.fromFuture(table.updateItem(user)).thenReturn(user)
    }
    */
}
