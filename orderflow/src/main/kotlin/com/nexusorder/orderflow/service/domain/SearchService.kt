package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.SearchRequest
import com.nexusorder.orderflow.model.payload.ShopResponse
import com.nexusorder.orderflow.service.storage.ProductStorageService
import com.nexusorder.orderflow.service.storage.ShopStorageService
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SearchService(
    private val shopStorageService: ShopStorageService,
    private val productStorageService: ProductStorageService
) {

    // 검색
    fun search(request: SearchRequest): Mono<List<ShopResponse>> {
        // 검색 대상이 없을 경우 빈 리스트를 반환합니다.
        if (StringUtils.isBlank(request.name) && StringUtils.isBlank(request.category)) {
            return Mono.just(emptyList())
        }

        return Mono.zip(
            // 가게 중 검색어에 해당하는 가게를 찾습니다.
            shopStorageService.findAll()
                .filter {
                    (StringUtils.isBlank(request.name) || it.name.contains(request.name, ignoreCase = true)) &&
                        (
                            StringUtils.isBlank(request.category) || it.category.contains(
                                request.category,
                                ignoreCase = true
                            )
                            )
                }
                .collectList(),
            // 상품 중 검색어에 해당하는 상품을 찾습니다.
            productStorageService.findAll()
                .filter {
                    (StringUtils.isBlank(request.name) || it.name.contains(request.name, ignoreCase = true)) &&
                        (
                            StringUtils.isBlank(request.category) || it.category.contains(
                                request.category,
                                ignoreCase = true
                            )
                            )
                }
                .map { it.shopId }
                .distinct()
                // 상품에 해당하는 가게를 찾습니다.
                .flatMap { shopStorageService.findById(it) }
                .collectList()
        ).flatMapMany { tuple ->
            // 가게와 상품을 합쳐서 중복을 제거합니다.
            Flux.fromIterable((tuple.t1 + tuple.t2).distinctBy { it.id })
        }.flatMap {
            // 가게에 해당하는 상품을 찾습니다.
            productStorageService.findAllByShopId(it.id)
                .collectList()
                // 가게와 상품을 합쳐서 반환합니다.
                .map { products -> ShopResponse.from(it, products) }
        }.collectList()
    }
}
