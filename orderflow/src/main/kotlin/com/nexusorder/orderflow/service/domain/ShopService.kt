package com.nexusorder.orderflow.service.domain

import com.nexusorder.orderflow.model.payload.ShopRequest
import com.nexusorder.orderflow.model.payload.ShopResponse
import com.nexusorder.orderflow.model.storage.Shop
import com.nexusorder.orderflow.service.storage.ProductStorageService
import com.nexusorder.orderflow.service.storage.ShopStorageService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ShopService(
    private val shopStorageService: ShopStorageService,
    private val productStorageService: ProductStorageService
) {

    // 가게 상세 조회
    fun findById(id: String): Mono<ShopResponse> {
        return shopStorageService.findById(id)
            .flatMap {
                // 가게에 해당하는 상품을 찾습니다.
                productStorageService.findAllByShopId(it.id)
                    .collectList()
                    .map { products ->
                        // 가게와 상품을 합쳐서 반환합니다.
                        ShopResponse.from(it, products)
                    }
            }
    }

    // 가게 목록 조회
    fun findAll(): Mono<List<ShopResponse>> {
        return shopStorageService.findAll()
            .flatMap {
                // 가게에 해당하는 상품을 찾습니다.
                productStorageService.findAllByShopId(it.id)
                    .collectList()
                    .map { products ->
                        // 가게와 상품을 합쳐서 반환합니다.
                        ShopResponse.from(it, products)
                    }
            }.collectList()
    }

    // 가게 생성
    fun save(@RequestBody @Valid request: ShopRequest): Mono<ShopResponse> {
        return shopStorageService.save(
            // 요청을 가게 모델로 변환합니다.
            Shop.from(request)
        ).map {
            ShopResponse.from(it, listOf())
        }
    }

    // 추천 가게 조회
    fun recommend(@RequestParam @Valid @Min(1) count: Int = 1): Mono<List<ShopResponse>> {
        return shopStorageService.findAll()
            .collectList().map { shops ->
                // 가게 목록을 무작위로 배열한 후 count 만큼 반환합니다.
                shops.shuffled().take(count)
            }.flatMapMany {
                Flux.fromIterable(it)
            }.flatMap {
                // 가게에 해당하는 상품을 찾습니다.
                productStorageService.findAllByShopId(it.id).collectList()
                    .map { products ->
                        // 가게와 상품을 합쳐서 반환합니다.
                        ShopResponse.from(it, products)
                    }
            }.collectList()
    }
}
