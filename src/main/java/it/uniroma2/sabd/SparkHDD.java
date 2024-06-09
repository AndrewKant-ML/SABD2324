package it.uniroma2.sabd;

import it.uniroma2.sabd.types.CsvParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple5;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SparkHDD {

    private static final String FILE_PATH = "hdfs://master:54310/data.csv";

    public static void main(String[] args) {

        // Open SparkSession with given configuration
        SparkConf conf = new SparkConf()
                .setMaster("spark://master:7077")
                .setAppName("SMART Data Analysis");
        SparkSession sparkSession = SparkSession.builder().config(conf).getOrCreate();

        // Load data from HDFS and take only the first five columns
        JavaRDD<String> fileContent = sparkSession.read().textFile(FILE_PATH).javaRDD();
        JavaRDD<Tuple5<String, String, String, Long, Long>> value = fileContent
                .map(CsvParser::parseCSV)
                .filter(Objects::nonNull)
                .map(x -> new Tuple5<>(x.getTimestamp(), x.getSerialNumber(), x.getModel(), x.getFailure(), x.getVault_id()));

        // Execute first query
        executeQuery1(value, sparkSession);

        // Execute second query - part 1
        executeQuery2_1(value, sparkSession);

        // Execute second query - part 2
        executeQuery2_2(value, sparkSession);

        // Close Spark session
        sparkSession.close();
    }

    private static void executeQuery1(JavaRDD<Tuple5<String, String, String, Long, Long>> value, SparkSession sparkSession) {
        List<Tuple3<String, Long, Long>> resultsList = Queries.query1(value);
        // Convert date format
        List<Tuple3<String, Long, Long>> converted = new ArrayList<>();
        SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat to = new SimpleDateFormat("dd-MM-yyyy");
        resultsList.forEach(t ->
                {
                    try {
                        converted.add(new Tuple3<>(to.format(from.parse(t._1())), t._2(), t._3()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        Dataset<Tuple3<String, Long, Long>> query1Dataset = sparkSession.createDataset(converted, Encoders.tuple(Encoders.STRING(), Encoders.LONG(), Encoders.LONG()));
        query1Dataset.repartition(1)
                .withColumnRenamed("_1", "DD-MM-YYYY")
                .withColumnRenamed("_2", "vault_id")
                .withColumnRenamed("_3", "failures_count")
                .write()
                .option("header", true)
                .format("com.databricks.spark.csv")
                .save("hdfs://master:54310/out_1.csv");
    }

    private static void executeQuery2_1(JavaRDD<Tuple5<String, String, String, Long, Long>> value, SparkSession sparkSession) {
        List<Tuple2<Long, String>> resultsList = Queries.query2First(value);
        resultsList.forEach(t -> System.out.println(t._1() + ", " + t._2()));
        Dataset<Tuple2<Long, String>> query1Dataset = sparkSession.createDataset(resultsList, Encoders.tuple(Encoders.LONG(), Encoders.STRING()));
        query1Dataset.repartition(1)
                .withColumnRenamed("_1", "model")
                .withColumnRenamed("_2", "failures_count")
                .write()
                .option("header", true)
                .format("com.databricks.spark.csv")
                .save("hdfs://master:54310/out_21.csv");
    }

    private static void executeQuery2_2(JavaRDD<Tuple5<String, String, String, Long, Long>> value, SparkSession sparkSession) {
        List<Tuple3<Long, Long, String>> resultsList = Queries.query2Second(value);
        resultsList.forEach(t -> System.out.println(t._1() + ", " + t._2() + ", " + t._3()));
        Dataset<Tuple3<Long, Long, String>> query1Dataset = sparkSession.createDataset(resultsList, Encoders.tuple(Encoders.LONG(), Encoders.LONG(), Encoders.STRING()));
        query1Dataset.repartition(1)
                .withColumnRenamed("_1", "vault_id")
                .withColumnRenamed("_2", "failures_count")
                .withColumnRenamed("_3", "list_of_models")
                .write()
                .option("header", true)
                .format("com.databricks.spark.csv")
                .save("hdfs://master:54310/out_22.csv");
    }
}
