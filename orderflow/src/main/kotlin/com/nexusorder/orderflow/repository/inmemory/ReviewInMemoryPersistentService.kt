package com.nexusorder.orderflow.repository.inmemory

import com.nexusorder.orderflow.model.storage.Review
import org.springframework.stereotype.Service

@Service
class ReviewInMemoryPersistentService : AbstractInMemoryPersistentRepository<Review>()
