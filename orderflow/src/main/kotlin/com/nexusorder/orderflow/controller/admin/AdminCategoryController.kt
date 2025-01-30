package com.nexusorder.orderflow.controller.admin

import com.nexusorder.orderflow.model.storage.Category
import com.nexusorder.orderflow.service.storage.CategoryStorageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/admin/api/v1/categories")
class AdminCategoryController(
    private val categoryStorageService: CategoryStorageService
) {

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: String): Mono<Category> {
        return categoryStorageService.findById(id)
    }

    @GetMapping
    fun getAllCategories(): Flux<Category> {
        return categoryStorageService.findAll()
    }

    @PostMapping
    fun createCategory(@RequestBody category: Category): Mono<Category> {
        return categoryStorageService.save(category)
    }

    @GetMapping("/exists/{id}")
    fun categoryExists(@PathVariable id: String): Mono<Boolean> {
        return categoryStorageService.existsById(id)
    }
}
