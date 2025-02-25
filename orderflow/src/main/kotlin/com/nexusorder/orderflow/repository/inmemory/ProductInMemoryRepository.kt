package com.nexusorder.orderflow.repository.inmemory

import com.nexusorder.orderflow.model.storage.Product
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ProductInMemoryRepository : AbstractInMemoryPersistentRepository<Product>() {

    fun findAllByShopId(shopId: String): Flux<Product> {
        return Flux.fromIterable(this.db.values.filter { it.shopId == shopId })
    }
}
