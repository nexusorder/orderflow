REGION=ap-northeast-2

# 1. VPC 생성
aws ec2 create-vpc --cidr-block 10.0.0.0/16 --query Vpc.VpcId --output json 
aws ec2 create-tags --resources $VPC_ID --tags Key=Name,Value=MyVPC
# 2. 서브넷 생성
VPC_ID="vpc-0d347336d0404454e"
aws ec2 create-subnet --vpc-id $VPC_ID --cidr-block 10.0.1.0/24 --query Subnet.SubnetId --output json
SUBNET_ID="subnet-0d34de306c3202ffe"
aws ec2 create-tags --resources $SUBNET_ID --tags Key=Name,Value=MySubnet

# 3. 인터넷 게이트웨이 생성 및 연결
aws ec2 create-internet-gateway --query InternetGateway.InternetGatewayId --output json
IGW_ID="igw-0a70a4100c1c8426e"
aws ec2 attach-internet-gateway --vpc-id $VPC_ID --internet-gateway-id $IGW_ID
aws ec2 create-tags --resources $IGW_ID --tags Key=Name,Value=MyInternetGateway

# 4. 라우팅 테이블 생성 및 설정
aws ec2 create-route-table --vpc-id $VPC_ID --query RouteTable.RouteTableId --output text
RTB_ID="rtb-0a740c8a3e07a4514"
aws ec2 create-route --route-table-id $RTB_ID --destination-cidr-block 0.0.0.0/0 --gateway-id $IGW_ID
aws ec2 associate-route-table --subnet-id $SUBNET_ID --route-table-id $RTB_ID
aws ec2 create-tags --resources $RTB_ID --tags Key=Name,Value=MyRouteTable

# 5-1. 보안 그룹 생성
aws ec2 create-security-group \
  --group-name MyDynamoDBSecurityGroup \
  --description "Security group for external DynamoDB access" \
  --vpc-id $VPC_ID
SECURITY_GROUP_ID="sg-0b585819c7bc9a0ac"
aws ec2 create-tags --resources $SECURITY_GROUP_ID --tags Key=Name,Value=MyDynamoDBSecurityGroup

# 5-2. 보안 그룹 ID 확인
aws ec2 describe-security-groups \
  --filters Name=vpc-id,Values=$VPC_ID Name=group-name,Values=MyDynamoDBSecurityGroup \
  --query "SecurityGroups[0].GroupId" \
  --output json

# 5-3. 인바운드 규칙 추가
aws ec2 authorize-security-group-ingress \
  --group-id $SECURITY_GROUP_ID \
  --protocol tcp \
  --port 443 \
  --cidr 0.0.0.0/0

# 5-4. 아웃바운드 규칙 추가
aws ec2 authorize-security-group-egress \
  --group-id $SECURITY_GROUP_ID \
  --protocol tcp \
  --port 443 \
  --cidr 0.0.0.0/0

# 6-1. VPC 엔드포인트 생성
aws ec2 create-vpc-endpoint \
  --vpc-id $VPC_ID \
  --service-name com.amazonaws.$REGION.dynamodb \
  --vpc-endpoint-type Interface \
  --subnet-id $SUBNET_ID \
  --security-group-id $SECURITY_GROUP_ID
VPC_ENDPOINT_ID="vpce-0a4e85bab642abaed"
aws ec2 create-tags --resources $VPC_ENDPOINT_ID --tags Key=Name,Value=MyDynamoDBEndpoint

# 6-2. 6 오류 시 확인 및 수정
aws ec2 describe-vpc-attribute \
  --vpc-id $VPC_ID \
  --attribute enableDnsSupport

aws ec2 modify-vpc-attribute \
  --vpc-id $VPC_ID \
  --enable-dns-support

aws ec2 describe-vpc-attribute \
  --vpc-id $VPC_ID \
  --attribute enableDnsHostnames

aws ec2 modify-vpc-attribute \
  --vpc-id $VPC_ID \
  --enable-dns-hostnames

# 6-3. 엔드포인트 URL 확인
aws ec2 describe-vpc-endpoints \
  --vpc-endpoint-ids $VPC_ENDPOINT_ID \
  --query 'VpcEndpoints[0].DnsEntries[0].DnsName' \
  --output json

#7. DynamoDB 접속 테스트
ENDPOINT_DNS_NAME="vpce-0a4e85bab642abaed-fddx3j5v.dynamodb.ap-northeast-2.vpce.amazonaws.com"
aws dynamodb list-tables --endpoint-url https://$ENDPOINT_DNS_NAME


#====================================================================================================
# clean-up

# 1. VPC 엔드포인트 삭제
aws ec2 delete-vpc-endpoints --vpc-endpoint-ids $VPC_ENDPOINT_ID

# 2. 보안 그룹 삭제
aws ec2 delete-security-group --group-id $SECURITY_GROUP_ID

# 3. 서브넷에서 라우팅 테이블 연결 해제
aws ec2 disassociate-route-table --association-id $(aws ec2 describe-route-tables \
  --route-table-ids $RTB_ID \
  --query 'RouteTables[0].Associations[0].RouteTableAssociationId' \
  --output text)

# 4. 라우팅 테이블 삭제
aws ec2 delete-route-table --route-table-id $RTB_ID

# 5. 인터넷 게이트웨이 분리 및 삭제
aws ec2 detach-internet-gateway --vpc-id $VPC_ID --internet-gateway-id $IGW_ID
aws ec2 delete-internet-gateway --internet-gateway-id $IGW_ID

# 6. 서브넷 삭제
aws ec2 delete-subnet --subnet-id $SUBNET_ID

# 7. VPC 삭제
aws ec2 delete-vpc --vpc-id $VPC_ID