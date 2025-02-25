#!/bin/sh
profile="dev"
helm upgrade --install orderflow ../chart --values ../chart/values/$profile/values.yaml