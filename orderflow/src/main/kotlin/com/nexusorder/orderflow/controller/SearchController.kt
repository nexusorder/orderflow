package com.nexusorder.orderflow.controller

import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.model.payload.SearchRequest
import com.nexusorder.orderflow.model.payload.ShopResponse
import com.nexusorder.orderflow.service.domain.SearchService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/search")
class SearchController(
    private val searchService: SearchService
) {

    @PostMapping
    fun search(@RequestBody request: SearchRequest): Mono<ApiResponse<List<ShopResponse>>> {
        return searchService.search(request)
            .map { ApiResponse.success(it) }
    }
}
