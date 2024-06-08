package it.uniroma2.sabd;

import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple5;

import java.util.List;

public class Queries {

    public static List<Tuple3<String, Long, Long>> query1(JavaRDD<Tuple5<String, String, String, Long, Long>> data) {

        return data
                .filter(row -> row._4() != 0)
                .mapToPair(row -> new Tuple2<>(new Tuple2<>(row._1(), row._5()), 1L))
                .reduceByKey(Long::sum)
                .filter(row -> row._2() >= 2 && row._2() <= 4)
                .map(row -> new Tuple3<>(row._1()._1(), row._1()._2(), row._2()))
                .sortBy(Tuple3::_1, true, 1)
                .collect();
    }

    public static void query2() {

    }
}
