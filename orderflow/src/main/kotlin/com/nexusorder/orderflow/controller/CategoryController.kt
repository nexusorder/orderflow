package com.nexusorder.orderflow.controller

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.CategoryResponse
import com.nexusorder.orderflow.service.domain.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @GetMapping
    fun findAll(): Mono<ApiResponse<List<CategoryResponse>>> {
        return categoryService.findAll()
            .map { ApiResponse.success(it) }
    }
}
