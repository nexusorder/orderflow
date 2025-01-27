# Orderflow
> The next order service

Backend Application 프로젝트 폴더입니다.
Spring boot framework로 만들어졌습니다.

## Getting started
```
# requires JDK 17
echo "org.gradle.java.home=$(/usr/libexec/java_home -v 17)" > gradle.properties

# requires brew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# requires podman (docker도 가능)
brew install podman
```

### 실행
```sh
./gradlew bootRun
```

### 빌드
```sh
./gradlew clean build
```

### 테스트
```sh
./gradlew test
```
