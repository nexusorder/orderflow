## DynamoDB
기본 설정은 Spring Boot에서 명령형으로 초기화합니다(TestDataConfig 참조).
DynamoDB를 직접 다루기 위해서는 해당 폴더의 스크립트들을 참조해주세요.
aws cli가 설치되어 있어야합니다.

### Dynamodb 공용 Endpoint 조회
```console
$ aws dynamodb describe-endpoints --region ap-northeast-2
{
    "Endpoints": [
        {
            "Address": "dynamodb.ap-northeast-2.amazonaws.com",
            "CachePeriodInMinutes": 1440
        }
    ]
}
```
