package com.nexusorder.orderflow.repository.inmemory

import com.nexusorder.orderflow.model.storage.Category
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CategoryInMemoryRepository : AbstractInMemoryPersistentRepository<Category>() {

    fun findByKey(key: String): Mono<Category> {
        return Mono.justOrEmpty(
            db.values.find { it.key == key }
        )
    }
}
