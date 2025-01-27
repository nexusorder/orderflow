## AWS Scripts
> See also: https://docs.aws.amazon.com/eks/latest/userguide/quickstart.html#quickstart-deploy-game

### Requirements
[eksctl](https://github.com/eksctl-io/eksctl/releases)를 다운받아주세요.
~/.bash_profile 또는 ~/.zshrc 같은 파일의 마지막에 설정을 추가해서
PATH에 eksctl이 담긴 폴더를 추가해주세요.

```sh
# ~/.zshrc 예시
export PATH="/Users/user/bin:$PATH"
```

### 클러스터 생성
```sh
./create-cluster.sh
```

### 클러스터 삭제
```sh
./delete-cluster.sh
```
