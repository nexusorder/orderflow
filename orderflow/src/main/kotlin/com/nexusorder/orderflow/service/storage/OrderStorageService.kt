package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Order
import com.nexusorder.orderflow.repository.dynamodb.OrderDynamoDBRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderStorageService(
    private val orderRepository: OrderDynamoDBRepository
) {

    fun findById(id: String): Mono<Order> {
        return orderRepository.findById(id)
    }

    fun findAllByMemberId(memberId: String): Flux<Order> {
        return orderRepository.findAllByMemberId(memberId)
    }

    fun existsById(id: String): Mono<Boolean> {
        return orderRepository.existsById(id)
    }

    fun findAll(): Flux<Order> {
        return orderRepository.findAll()
    }

    fun save(entity: Order): Mono<Order> {
        return orderRepository.save(entity)
    }
}
