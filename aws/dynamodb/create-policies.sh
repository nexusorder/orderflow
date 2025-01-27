TABLE_NAMES=("category" "member" "order" "product" "shop")
USERID=$(aws sts get-caller-identity --query Account --output text)

create_policy() {
  POLICY_NAME="DynamoDBAccessPolicy-$1"
  POLICY_ARN="arn:aws:iam::$USERID:policy/$POLICY_NAME"
  aws iam create-policy --policy-name $POLICY_NAME --policy-document '{ 
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "dynamodb:GetItem",
                "dynamodb:PutItem",
                "dynamodb:UpdateItem",
                "dynamodb:DeleteItem",
                "dynamodb:Query",
                "dynamodb:Scan"
            ],
            "Resource": "arn:aws:dynamodb:ap-northeast-2:'"$USERID"':table/'"$1"'"
        }
    ]
  }'

  ROLE_NAME="DynamoDBAccessRole-$1"
  aws iam attach-role-policy --role-name $ROLE_NAME --policy-arn $POLICY_ARN
}

for table in "${TABLE_NAMES[@]}"; do
  create_policy "$table"
done