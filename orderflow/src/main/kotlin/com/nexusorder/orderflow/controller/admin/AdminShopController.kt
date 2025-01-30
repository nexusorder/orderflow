package com.nexusorder.orderflow.controller.admin

import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.service.storage.ShopStorageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/admin/api/v1/shops")
class AdminShopController(
    private val shopStorageService: ShopStorageService
) {

    @GetMapping("/{id}")
    fun getShopById(@PathVariable id: String): Mono<Shop> {
        return shopStorageService.findById(id)
    }

    @GetMapping
    fun getAllShops(): Flux<Shop> {
        return shopStorageService.findAll()
    }

    @PostMapping
    fun createShop(@RequestBody shop: Shop): Mono<Shop> {
        return shopStorageService.save(shop)
    }

    @GetMapping("/exists/{id}")
    fun shopExists(@PathVariable id: String): Mono<Boolean> {
        return shopStorageService.existsById(id)
    }
}
