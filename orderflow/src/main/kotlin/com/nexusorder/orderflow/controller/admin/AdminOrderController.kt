package com.nexusorder.orderflow.controller.admin

import com.nexusorder.orderflow.model.storage.Order
import com.nexusorder.orderflow.service.storage.OrderStorageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/admin/api/v1/orders")
class AdminOrderController(
    private val orderStorageService: OrderStorageService
) {

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: String): Mono<Order> {
        return orderStorageService.findById(id)
    }

    @GetMapping
    fun getAllOrders(): Flux<Order> {
        return orderStorageService.findAll()
    }

    @PostMapping
    fun createOrder(@RequestBody order: Order): Mono<Order> {
        return orderStorageService.save(order)
    }

    @GetMapping("/exists/{id}")
    fun orderExists(@PathVariable id: String): Mono<Boolean> {
        return orderStorageService.existsById(id)
    }
}
