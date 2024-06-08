package it.uniroma2.sabd;

import it.uniroma2.sabd.utils.OutletParser;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple3;
import scala.Tuple5;

import java.util.List;
import java.util.Objects;

public final class SparkHDD {

    private static final String FILE_PATH = "hdfs://master:54310/data.csv";

    public static StructType hddDataSchema() {
        return DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("date", DataTypes.StringType, false),
                DataTypes.createStructField("serial_number", DataTypes.StringType, false),
                DataTypes.createStructField("model", DataTypes.StringType, false),
                DataTypes.createStructField("failure", DataTypes.LongType, false),
                DataTypes.createStructField("vault_id", DataTypes.LongType, false),
        });
    }

    public static void main(String[] args) {

        String outputPath = "output.csv";
        if (args.length > 0) outputPath = args[0];

        // Open SparkSession with given configuration
        SparkConf conf = new SparkConf()
                .setMaster("spark://master:7077")
                .setAppName("SMART Data Analysis");
        SparkSession sparkSession = SparkSession.builder().config(conf).getOrCreate();

        // Load data from HDFS
        Dataset<Row> df = sparkSession.read()
                .format("csv")
                .option("header", true)
                .schema(hddDataSchema())
                .option("dateFormat", "yyyy-MM-ddThh:mm:ss.sssZ")
                .load(FILE_PATH);

        // Optional instructions to show data
        // df.show();
        // df.printSchema();

        // Convert Dataframe to Dataset
        /*Dataset<DataRow> ds = df.map(
                new DataRowMapper(),
                Encoders.bean(DataRow.class)
        );*/

        JavaRDD<String> fileContent = sparkSession.read().textFile(FILE_PATH).javaRDD();

        JavaRDD<Tuple5<String, String, String, Long, Long>> value = fileContent
                .map(OutletParser::parseCSV)
                .filter(Objects::nonNull)
                .map(x -> new Tuple5<>(x.getTimestamp(), x.getSerialNumber(), x.getModel(), x.getFailure(), x.getVault_id()));

        List<Tuple3<String, Long, Long>> t = Queries.query1(value);

        System.out.println(t.size());
        t.forEach(x -> System.err.println(x._1() + " " + x._2() + " " + x._3()));

        sparkSession.close();

        //Dataset<Row> fileContent = sparkSession.read().csv(FILE_PATH); //.toDF()


        /*try (JavaSparkContext sparkContext = new JavaSparkContext(conf)) {
            sparkContext.setLogLevel("ERROR");

            JavaRDD<String> file = sparkContext
            JavaRDD<Tuple5<LocalDateTime, String, String, Boolean, Long>> value = file
                    .map(OutletParser::parseCSV)
                    .filter(Objects::nonNull)
                    .map(x -> new Tuple5<>(x.getTimestamp(), x.getSerialNumber(), x.getModel(), x.getFailure(), x.getVault_id()));

            System.out.println("Spark SQL first test - query 1");
            //SQLQueries.query1(value);
        }*/
    }
}
