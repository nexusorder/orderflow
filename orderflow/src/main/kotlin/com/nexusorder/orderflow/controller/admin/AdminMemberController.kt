package com.nexusorder.orderflow.controller.admin

import com.nexusorder.orderflow.model.storage.Member
import com.nexusorder.orderflow.service.storage.MemberStorageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/admin/api/v1/members")
class AdminMemberController(
    private val memberStorageService: MemberStorageService
) {

    @GetMapping("/{id}")
    fun getMemberById(@PathVariable id: String): Mono<Member> {
        return memberStorageService.findById(id)
    }

    @GetMapping
    fun getAllMembers(): Flux<Member> {
        return memberStorageService.findAll()
    }

    @PostMapping
    fun createMember(@RequestBody member: Member): Mono<Member> {
        return memberStorageService.save(member)
    }

    @GetMapping("/exists/{id}")
    fun memberExists(@PathVariable id: String): Mono<Boolean> {
        return memberStorageService.existsById(id)
    }
}
