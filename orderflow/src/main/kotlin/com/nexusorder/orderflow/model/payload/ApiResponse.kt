package com.nexusorder.orderflow.model.payload

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val errorCode: Int?,
    val errorMessage: String?
) {

    companion object {

        fun <T> success(data: T?): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data,
                errorCode = null,
                errorMessage = null
            )
        }

        fun <T> error(data: T?, errorCode: Int?, errorMessage: String?): ApiResponse<T> {
            return ApiResponse(
                success = false,
                data = data,
                errorCode = errorCode,
                errorMessage = errorMessage
            )
        }
    }
}
