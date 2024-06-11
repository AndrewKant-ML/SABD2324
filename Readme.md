# Usage
## Containers
To start Docker containers, run
```
./Scripts/run.sh
```
To stop containers, run
```
./Scripts/stop.sh
```
## HDFS
To start HDFS cluster:
```
./Scripts/start-hdfs.sh
```
To load the dataset into HDFS, run
```
./Scripts/load-data.sh <path-to-dataset>
```
To stop HDFS cluster:
```
./Scripts/stop-hdfs.sh
```
## Application
To run maven build:
```
./make.sh
```
To run application:
```
./deploy.sh
```