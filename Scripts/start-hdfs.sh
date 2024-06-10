#!/bin/bash

HDFS_MASTER_NAME=hdfs-master

# Format HDFS for first-time execution
docker compose exec $HDFS_MASTER_NAME hdfs namenode -format

# Starts HDFS master
docker compose exec $HDFS_MASTER_NAME /usr/local/hadoop/sbin/start-dfs.sh
