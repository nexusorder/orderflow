package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.LoginRequest
import com.nexusorder.orderflow.model.payload.ProfileResponse
import com.nexusorder.orderflow.model.payload.SignupRequest
import com.nexusorder.orderflow.model.storage.Member
import com.nexusorder.orderflow.service.storage.MemberStorageService
import com.nexusorder.orderflow.util.HashUtil
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService(val memberStorageService: MemberStorageService) {

    fun login(request: LoginRequest): Mono<Member> {
        return memberStorageService.findByLogin(request.login)
            .mapNotNull {
                if (it.password == HashUtil.getPasswordHash(request.password)) {
                    it
                } else {
                    null
                }
            }
    }

    fun signup(request: SignupRequest): Mono<Member> {
        return memberStorageService.existsByLogin(request.login.trim())
            .flatMap {
                if (it) {
                    Mono.empty()
                } else {
                    memberStorageService.save(
                        Member(
                            login = request.login.trim(),
                            password = HashUtil.getPasswordHash(request.login.trim() + request.password),
                            name = request.name.trim(),
                            nickname = request.nickname.trim(),
                            email = request.email.trim(),
                            phone = request.phone.trim(),
                            address = request.address.trim(),
                            latitude = request.latitude.toString(),
                            longitude = request.longitude.toString(),
                        )
                    )
                }
            }
    }

    fun profile(id: String): Mono<ProfileResponse> {
        return memberStorageService.findById(id)
            .map { ProfileResponse.of(it) }
    }

    companion object {
        const val LOGIN_KEY = "login"
    }
}
