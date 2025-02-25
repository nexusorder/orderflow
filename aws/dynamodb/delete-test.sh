#!/bin/sh
aws dynamodb execute-statement --statement "DELETE FROM category WHERE id = 'test'"