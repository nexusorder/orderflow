package com.nexusorder.orderflow.config

import com.fasterxml.jackson.core.type.TypeReference
import com.nexusorder.orderflow.model.storage.AbstractCoreModel
import com.nexusorder.orderflow.model.storage.Category
import com.nexusorder.orderflow.model.storage.Member
import com.nexusorder.orderflow.model.storage.Product
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.repository.dynamodb.CategoryDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ProductDynamoDBRepository
import com.nexusorder.orderflow.repository.dynamodb.ShopDynamoDBRepository
import com.nexusorder.orderflow.util.CoreLogger
import com.nexusorder.orderflow.util.CoreObjectMapper
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.function.Function

@Configuration
@Profile("!test")
class TestDataConfig(
    private val memberRepository: MemberDynamoDBRepository,
    private val shopRepository: ShopDynamoDBRepository,
    private val productRepository: ProductDynamoDBRepository,
    private val categoryRepository: CategoryDynamoDBRepository,
    private val orderRepository: OrderDynamoDBRepository
) : InitializingBean {

    override fun afterPropertiesSet() {
        createTables()
        initDatasets()
    }

    private fun createTables() {
        Flux.merge(
            memberRepository.createTable(),
            shopRepository.createTable(),
            productRepository.createTable(),
            categoryRepository.createTable(),
            orderRepository.createTable()
        ).blockLast(Duration.ofSeconds(60))
    }

    private fun initDatasets() {
        initDataset("members.json", memberRepository, object : TypeReference<List<Member>>() {})
        initDataset("shops.json", shopRepository, object : TypeReference<List<Shop>>() {})
        initDataset("products.json", productRepository, object : TypeReference<List<Product>>() {})
        initDataset("categories.json", categoryRepository, object : TypeReference<List<Category>>() {})
    }

    private final fun <T : AbstractCoreModel> initDataset(
        filename: String,
        repository: ReactiveCrudRepository<T, String>,
        typeReference: TypeReference<List<T>> = object : TypeReference<List<T>>() {}
    ) {
        initDataset(filename, repository::save, typeReference)
    }

    private final fun <T : AbstractCoreModel> initDataset(
        filename: String,
        saveMethod: Function<T, Mono<T>>,
        typeReference: TypeReference<List<T>> = object : TypeReference<List<T>>() {}
    ) {
        val json = TestDataConfig::class.java.classLoader
            .getResource("dataset/$filename")?.readText()
            ?: throw Exception("$filename not found")
        CoreObjectMapper
            .readValue(json, typeReference)
            .let {
                Flux.fromIterable(it)
                    .concatMap { entity ->
                        saveMethod.apply(entity)
                            .onErrorResume {
                                CoreLogger.error("TestDataConfig", message = "Error while saving entity", data = entity, throwable = it)
                                Mono.empty()
                            }
                    }.blockLast(Duration.ofSeconds(60))
            }

        CoreLogger.info("TestDataConfig", message = "Dataset $filename initialized")
    }
}
