package com.nexusorder.orderflow.repository.inmemory

import com.nexusorder.orderflow.model.storage.Shop
import org.springframework.stereotype.Service

@Service
class ShopInMemoryRepository : AbstractInMemoryPersistentRepository<Shop>()
