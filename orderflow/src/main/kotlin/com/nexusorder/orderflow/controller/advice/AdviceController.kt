package com.nexusorder.orderflow.controller.advice

import com.nexusorder.orderflow.exception.MinimumOrderNotFulfilledException
import com.nexusorder.orderflow.model.payload.ApiResponse
import com.nexusorder.orderflow.util.CoreLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@ControllerAdvice
class AdviceController {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): Mono<ResponseEntity<ApiResponse<Void?>>> {
        return getErrorResponse(e, "an error occurred", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNotFound(e: Exception): Mono<ResponseEntity<ApiResponse<Void?>>> {
        return getErrorResponse(e, "an error occurred", HttpStatus.NOT_FOUND, stackTrace = false)
    }

    @ExceptionHandler(ServerWebInputException::class, MinimumOrderNotFulfilledException::class)
    fun handleBadRequest(e: Exception): Mono<ResponseEntity<ApiResponse<Void?>>> {
        return getErrorResponse(e, "an error occurred", HttpStatus.BAD_REQUEST)
    }

    private fun getErrorResponse(
        e: Exception,
        message: String = "an error occurred",
        httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        errorCode: Int? = null,
        errorMessage: String? = null,
        stackTrace: Boolean = true
    ): Mono<ResponseEntity<ApiResponse<Void?>>> {
        val preparedMessage = if (!stackTrace) {
            "$message: ${e.message}"
        } else {
            message
        }
        CoreLogger.error(
            "Advice", "", "", preparedMessage, if (stackTrace) e else null
        )
        return Mono.just(
            ResponseEntity.status(httpStatus)
                .body(
                    ApiResponse.error(null, errorCode ?: httpStatus.value(), errorMessage ?: httpStatus.name)
                )
        )
    }
}
