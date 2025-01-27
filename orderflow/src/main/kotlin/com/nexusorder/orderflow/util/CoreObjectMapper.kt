package com.nexusorder.orderflow.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object CoreObjectMapper : ObjectMapper() {

    init {
        registerKotlinModule()
    }
}
