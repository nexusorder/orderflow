#!/bin/sh
# aws configure
# aws eks list-clusters
# https://docs.aws.amazon.com/eks/latest/userguide/quickstart.html#quickstart-deploy-game
aws eks update-kubeconfig --name nexusorder --region ap-northeast-2
./helm-upgrade.sh
