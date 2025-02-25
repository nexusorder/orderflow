package com.nexusorder.orderflow.repository.inmemory

import com.nexusorder.orderflow.model.storage.Order
import org.springframework.stereotype.Service

@Service
class OrderInMemoryRepository : AbstractInMemoryPersistentRepository<Order>()
