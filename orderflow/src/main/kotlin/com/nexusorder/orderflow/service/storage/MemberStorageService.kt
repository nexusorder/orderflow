package com.nexusorder.orderflow.service.storage

import com.nexusorder.orderflow.model.storage.Member
import com.nexusorder.orderflow.repository.dynamodb.MemberDynamoDBRepository
import com.nexusorder.orderflow.service.domain.EncryptionService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MemberStorageService(
    private val memberRepository: MemberDynamoDBRepository,
    private val encryptionService: EncryptionService
) {

    fun findById(id: String): Mono<Member> {
        return memberRepository.findById(id)
            .map {
                it.copy(
                    email = encryptionService.decrypt(it.email),
                    phone = encryptionService.decrypt(it.phone),
                    address = encryptionService.decrypt(it.address),
                    latitude = encryptionService.decrypt(it.latitude),
                    longitude = encryptionService.decrypt(it.longitude),
                )
            }
    }

    fun findByLogin(login: String): Mono<Member> {
        return memberRepository.findByLogin(login)
            .map {
                it.copy(
                    email = encryptionService.decrypt(it.email),
                    phone = encryptionService.decrypt(it.phone),
                    address = encryptionService.decrypt(it.address),
                    latitude = encryptionService.decrypt(it.latitude),
                    longitude = encryptionService.decrypt(it.longitude),
                )
            }
    }

    fun existsById(id: String): Mono<Boolean> {
        return memberRepository.existsById(id)
    }

    fun existsByLogin(login: String): Mono<Boolean> {
        return memberRepository.existsByLogin(login)
    }

    fun findAll(): Flux<Member> {
        return memberRepository.findAll()
            .map {
                it.copy(
                    email = encryptionService.decrypt(it.email),
                    phone = encryptionService.decrypt(it.phone),
                    address = encryptionService.decrypt(it.address),
                    latitude = encryptionService.decrypt(it.latitude),
                    longitude = encryptionService.decrypt(it.longitude),
                )
            }
    }

    fun save(entity: Member): Mono<Member> {
        return memberRepository.save(
            entity.copy(
                email = encryptionService.encrypt(entity.email),
                phone = encryptionService.encrypt(entity.phone),
                address = encryptionService.encrypt(entity.address),
                latitude = encryptionService.encrypt(entity.latitude),
                longitude = encryptionService.encrypt(entity.longitude),
            )
        )
    }
}
