package com.nexusorder.orderflow.config

import com.nexusorder.orderflow.service.aws.CloudWatchLogService
import com.nexusorder.orderflow.util.CoreLogger
import io.micrometer.cloudwatch2.CloudWatchConfig
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry
import io.micrometer.core.instrument.Clock
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import java.time.Duration

// CloudWatch 설정
@Configuration
@Profile("!test")
class DefaultCloudWatchConfig(
    private val cloudWatchLogService: CloudWatchLogService,
    @Value("\${aws.cloudwatch.metrics.namespace}") private val namespace: String,
    @Value("\${aws.cloudwatch.metrics.step}") private val step: Long
) : InitializingBean {

    override fun afterPropertiesSet() {
        CoreLogger.cloudWatchLogService = cloudWatchLogService
    }

    @Bean
    fun cloudWatchConfig(): CloudWatchConfig {
        return CloudWatchConfig { key ->
            when (key) {
                "cloudwatch.namespace" -> namespace
                "cloudwatch.step" -> Duration.ofMinutes(step).toString()
                else -> null
            }
        }
    }

    @Bean
    fun cloudWatchAsyncClient(): CloudWatchAsyncClient {
        return CloudWatchAsyncClient.builder().build()
    }

    @Bean
    fun cloudWatchMeterRegistry(
        cloudWatchConfig: CloudWatchConfig,
        cloudWatchAsyncClient: CloudWatchAsyncClient
    ): CloudWatchMeterRegistry {
        return CloudWatchMeterRegistry(cloudWatchConfig, Clock.SYSTEM, cloudWatchAsyncClient)
    }
}
