#!/bin/sh
aws dynamodb execute-statement --statement "INSERT INTO category  \
                VALUE  \
                {'id': 'test', 'Artist':'No One You Know','SongTitle':'Call Me Today', 'AlbumTitle':'Somewhat Famous', 'Awards':'1'}"
