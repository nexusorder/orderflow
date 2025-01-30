package com.nexusorder.orderflow.config

import com.nexusorder.orderflow.util.CoreLogger
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class LoggingFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request: ServerHttpRequest = exchange.getRequest()
        val response: ServerHttpResponse = exchange.getResponse()
        val url = request.getURI().rawSchemeSpecificPart.replace(Regex("//[^/]+"), "")

        CoreLogger.info("LoggingFilter", message = "Request: %s %s".format(request.getMethod(), url))

        return chain.filter(exchange)
            .doOnSuccess { _ ->
                CoreLogger.info("LoggingFilter", message = "Response: %s %s %s".format(response.getStatusCode(), request.getMethod(), url))
            }
            .doOnError { e ->
                CoreLogger.error("LoggingFilter", message = "An error occurred", throwable = e)
            }
    }
}
