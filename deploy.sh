#!/bin/bash
$SPARK_HOME/bin/spark-submit --class="it.uniroma2.sabd.SparkHDD" --master "spark://spark-master:8080" target/test-1.0-SNAPSHOT.jar