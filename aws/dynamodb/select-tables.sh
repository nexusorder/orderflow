#!/bin/sh
TABLE_NAMES=("category" "member" "order" "product" "shop")

select_table() {
  printf "aws dynamodb execute-statement --statement \"SELECT * FROM $1\"\n"
  aws dynamodb execute-statement --statement "SELECT * FROM $1"
  printf "\n\n"
}

for table in "${TABLE_NAMES[@]}"; do
  select_table "$table"
done
