package com.nexusorder.orderflow.repository.inmemory

import com.nexusorder.orderflow.model.storage.Member
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MemberInMemoryRepository : AbstractInMemoryPersistentRepository<Member>() {

    fun findByLogin(login: String): Mono<Member> {
        return Mono.justOrEmpty(
            db.values.find { it.login == login }
        )
    }

    fun existsByLogin(login: String): Mono<Boolean> {
        return Mono.justOrEmpty(
            db.values.find { it.login == login } != null
        )
    }
}
