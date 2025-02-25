# Level 5: 운영과 유지보수
## Task 13
- 소제목: CI/CD 파이프라인 구축(Github Actions)
- 멘토 코멘트: PR이 머지되면 자동으로 클라우드에 배포되도록 합니다.
- 소요시간: 60분
- 답변 Type : 장문형

Github Actions를 이용하면 CI/CD를 쉽게 구축할 수 있습니다.
[Quickstart](https://docs.github.com/ko/actions/writing-workflows/quickstart) 문서를 확인하시면 간단한 사용법을 익히실 수 있습니다.
제공드린 `build.sh`, `push.sh`, `deploy.sh` 스크립트를 실행하도록
workflow를 작성해주세요.

- 정답:
```yml
name: Deploy

on:
  pull_request:

jobs:
  build:
    name: Build and Deploy
    runs-on: ubuntu-latest
    permissions:
      contents: write
      issues: write
      pull-requests: write
    steps:
      - name: Checkout
        uses: actions/checkout@v3
        with:
          clean: false

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'
  
      - name: Gradle Test
        run: |
          orderflow/gradlew test -p orderflow
      
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v1
        with:
          # Settings > Actions > Secrets에 필요한 정보를 등록합니다.
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ap-northeast-2

      - name: Build and Push Docker Image
        env:
          DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
          DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
          AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
          AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        id: build
        run: |
          echo "result=failure" >> "$GITHUB_OUTPUT"
          cd script
          ./build.sh
          ./push.sh "$DOCKER_USERNAME" "$DOCKER_PASSWORD"
          ./deploy.sh
          hostname=$(kubectl get ingress -o jsonpath="{.items[*].status.loadBalancer.ingress[*].hostname}")
          helm_status=$(helm status orderflow)
          echo "result=success" >> "$GITHUB_OUTPUT"
          echo "hostname=$hostname" >> "$GITHUB_OUTPUT"
          {
            echo "helm_status<<EOF"
            echo "$helm_status"
            echo "EOF"
          } >> "$GITHUB_OUTPUT"

      - name: Write Deploy Comment
        uses: actions/github-script@v7.0.1
        if: always() && github.event.pull_request.number
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          # comment 기능을 쓰려면 Settings > Actions > General > Workflow permissions > Read and write permissions > Save를 진행합니다.
          script: |
            const { GITHUB_SERVER_URL, GITHUB_REPOSITORY, GITHUB_RUN_ID, COMMENT_SUCCESS} = process.env
            const actionUrl = `${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}`
            const url = `/repos/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}/approvals`
            let result = "${{ steps.build.outputs.result}}"
            let comment = ""
            console.log(result)
            switch(result){
              case "success":
                comment = "Deploy successful"
                comment += "\n\nHostname: http://${{ steps.build.outputs.hostname }}"
                comment += "\n\n```yaml\n" + `${{ steps.build.outputs.helm_status }}` + "\n```"
                break;
              case "failure":
                comment = "Deploy failed"
                break;
              default:
                break;
            }
            github.rest.issues.createComment({
              issue_number: "${{ github.event.pull_request.number }}",
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: comment
            })
            if (result === "failure") {
              throw new Error("Deploy failed")
            }
```

## Task 14
- 소제목: 로그 관리
- 멘토 코멘트: AWS CloudWatch Logs를 이용해 로그를 적재하고 확인해봅니다.
- 소요시간: 60분
- 답변 Type : 장문형

애플리케이션을 운영하다보면 다양한 로그가 발생하고
중요한 로그는 Pod에 직접 접근하지 않더라도 간단하게 볼 수 있다면 많은 도움이 되는데요.
AWS에서는 CloudWatch의 Logs라는 솔루션을 통해 이를 제공하고 있습니다.
CloudWatch의 Logs에 LogGroup과 LogStream을 생성하고
애플리케이션 로그를 전송하도록 작업해주세요.

- 정답
```kt
// CloudWatch Log에 로그를 기록하는 서비스
@Service
class CloudWatchLogService(
    @Value("\${aws.cloudwatch.log.group-name}") private val groupName: String,
    @Value("\${aws.cloudwatch.log.stream-name}") private val streamName: String,
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
```

```yaml
# application.yml
aws:
  ...
  cloudwatch:
    logs:
      group-name: nexusorder
      stream-name: orderflow
```

## Task 15
- 소제목: 시스템 모니터링
- 멘토 코멘트: AWS CloudWatch를 이용해 시스템 지표를 확인해봅니다.
- 소요시간: 60분
- 답변 Type : 장문형

애플리케이션 로그뿐만 아니라 지표(metrics)도 중요한 측정 데이터입니다.

애플리케이션을 운영하다보면 다양한 로그가 발생하고
AWS에서는 CloudWatch의 Metrics라는 솔루션을 통해 이를 제공하고 있습니다.
CloudWatch의 Metrics에 Namespace를 생성하고
애플리케이션 로그를 전송하도록 작업해주세요.

- 정답

```kt
// CloudWatch 설정
@Configuration
class DefaultCloudWatchConfig(
    @Value("\${aws.cloudwatch.metrics.namespace}") private val namespace: String,
    @Value("\${aws.cloudwatch.metrics.step}") private val step: Long
) {

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

```

```yaml
# application.yml
aws:
  ...
  cloudwatch:
    metrics:
      namespace: nexusorder/orderflow
      step: 1
```