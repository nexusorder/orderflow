package com.nexusorder.orderflow.exception

class MinimumOrderNotFulfilledException(
    override val message: String = "Minimum order not fulfilled"
) : Exception()
