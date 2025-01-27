package com.nexusorder.orderflow.support

import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClientConfigurer
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.server.WebSession
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.core.publisher.Mono

// 세션 정보를 변경하는 WebTestClientConfigurer
class SessionMutator(
    private val attributes: Map<String, Any>
) : WebTestClientConfigurer {

    // Configuerer 추가 시 세션 정보 변경
    override fun afterConfigurerAdded(
        builder: WebTestClient.Builder,
        httpHandlerBuilder: WebHttpHandlerBuilder?,
        connector: ClientHttpConnector?
    ) {
        val sessionMutatorFilter = SessionMutatorFilter(attributes)
        httpHandlerBuilder!!.filters { filters: MutableList<WebFilter?> ->
            filters.add(0, sessionMutatorFilter)
        }
    }

    // 세션 정보 변경하는 WebFilter
    private class SessionMutatorFilter(
        private val attributes: Map<String, Any>
    ) : WebFilter {
        override fun filter(exchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> {
            return exchange.session
                .doOnNext { webSession: WebSession ->
                    // attributes에 세션 정보 추가
                    webSession.attributes.putAll(attributes)
                }.then(
                    webFilterChain.filter(exchange)
                )
        }
    }
}
