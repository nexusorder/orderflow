package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Review
import com.nexusorder.orderflow.repository.dynamodb.ReviewDynamoDBRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReviewStorageService(
    private val reviewRepository: ReviewDynamoDBRepository
) {

    fun findById(id: String): Mono<Review> {
        return reviewRepository.findById(id)
    }

    fun existsById(id: String): Mono<Boolean> {
        return reviewRepository.existsById(id)
    }

    fun findAll(): Flux<Review> {
        return reviewRepository.findAll()
    }

    fun save(entity: Review): Mono<Review> {
        return reviewRepository.save(entity)
    }
}
