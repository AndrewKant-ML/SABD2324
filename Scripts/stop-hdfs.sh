#!/bin/bash

# Stop HDFS master
docker compose exec hdfs-master /usr/local/hadoop/sbin/stop-dfs.sh