TABLE_NAMES=("category" "member" "order" "product" "shop")

delete_table() {
  aws dynamodb delete-table --table-name "$1" --output json
}

for table in "${TABLE_NAMES[@]}"; do
  delete_table "$table"
done
