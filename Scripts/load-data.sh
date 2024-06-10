#!/bin/bash

HDFS_MASTER_NAME=hdfs-master

if [[ -z "$1" ]]; then
  echo "Usage: ./start-hdfs.sh <path-to-dataset>"
  exit 0
fi;

# Creates input and output folders in HDFS
docker compose exec $HDFS_MASTER_NAME hdfs dfs -mkdir /data
docker compose exec $HDFS_MASTER_NAME hdfs dfs -mkdir /out

# Grant permission on HDFS folders
docker compose exec $HDFS_MASTER_NAME hdfs dfs -chmod 777 /data
docker compose exec $HDFS_MASTER_NAME hdfs dfs -chmod 777 /out

# Copy dataset to HDFS master container
docker compose cp "$1" $HDFS_MASTER_NAME:/data.csv

# Load dataset into HDFS
docker compose exec $HDFS_MASTER_NAME hdfs dfs -put data.csv /data/data.csv