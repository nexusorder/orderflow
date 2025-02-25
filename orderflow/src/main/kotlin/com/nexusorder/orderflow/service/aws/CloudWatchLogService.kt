package com.nexusorder.orderflow.service.aws

import com.nexusorder.orderflow.util.CoreLogger
import com.nexusorder.orderflow.util.CoreObjectMapper
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent
import software.amazon.awssdk.services.cloudwatchlogs.model.ResourceAlreadyExistsException

// CloudWatch Log에 로그를 기록하는 서비스
@Service
@Profile("!test")
class CloudWatchLogService(
    @Value("\${aws.cloudwatch.logs.group-name}") private val groupName: String,
    @Value("\${aws.cloudwatch.logs.stream-name}") private val streamName: String,
    @Value("\${aws.region}") private val region: String
) : InitializingBean, DisposableBean {
    private val logsClient: CloudWatchLogsClient = CloudWatchLogsClient.builder()
        .region(Region.of(region))
        .build()

    // 로그 이벤트를 CloudWatch Log에 기록
    // 기록은 message에 대한 JSON 형태로 이루어짐
    fun putLogEvents(message: Any) {
        val describeLogStreamsResponse: DescribeLogStreamsResponse =
            logsClient.describeLogStreams {
                it.logGroupName(groupName)
                    .logStreamNamePrefix(streamName)
                    .build()
            }

        val sequenceTokenVal = describeLogStreamsResponse.logStreams()?.get(0)?.uploadSequenceToken()
        val inputLogEvent =
            InputLogEvent.builder()
                .message(CoreObjectMapper.writeValueAsString(message))
                .timestamp(System.currentTimeMillis())
                .build()

        logsClient.putLogEvents {
            it.logEvents(listOf(inputLogEvent))
                .logGroupName(groupName)
                .logStreamName(streamName)
                .sequenceToken(sequenceTokenVal)
                .build()
        }
    }

    // 초기화 시 CloudWatch LogGroup과 LogStream을 생성
    override fun afterPropertiesSet() {
        CoreLogger.info("CloudWatchLogService", message = "groupName: $groupName, streamName: $streamName, region: $region", sendToCloudWatch = false)

        try {
            logsClient.createLogGroup { it.logGroupName(groupName).build() }
        } catch (e: Exception) {
            if (e !is ResourceAlreadyExistsException) { throw e }
        }

        try {
            logsClient.createLogStream { it.logGroupName(groupName).logStreamName(streamName).build() }
        } catch (e: Exception) {
            if (e !is ResourceAlreadyExistsException) { throw e }
        }
    }

    // 종료 시 CloudWatch Logs Client를 닫음
    override fun destroy() {
        logsClient.close()
    }
}
