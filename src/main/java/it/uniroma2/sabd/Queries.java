package it.uniroma2.sabd;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Queries {

    private static final int TOP_N = 10;

    /**
     * Query 1
     * For each day, for each vault (refer to the vault id field), calculate the
     * total number of failures. Determine the list of vaults that experienced
     * exactly 4, 3 and 2 failures.
     *
     * @param data  the provided data
     * @return      a List of (timestamp, vault_id, failures_count) tuples
     */
    public static List<Tuple3<String, Long, Long>> query1(JavaRDD<Tuple5<String, String, String, Long, Long>> data) {

        return data
                // Discard row without failures
                .filter(row -> row._4() != 0)
                // Map each row in a ((timestamp, vault_id), 1) tuple
                .mapToPair(row -> new Tuple2<>(new Tuple2<>(row._1(), row._5()), 1L))
                // Sum failures for each (timestamp, vault_id) couple
                .reduceByKey(Long::sum)
                // Take only entries with 2, 3 or 4 failures
                .filter(row -> row._2() >= 2 && row._2() <= 4)
                // Map the result to a (timestamp, vault_id, failures_count) tuple
                .map(row -> new Tuple3<>(row._1()._1(), row._1()._2(), row._2()))
                // Sort the result by timestamp
                .sortBy(Tuple3::_1, true, 1)
                // Collect the results
                .collect();
    }

    /**
     * Query 2_1
     * Calculate the ranking of the 10 hard disk drive models that have suffered the most failures. The
     * ranking must report the hard disk model and the total number of failures suffered by hard disks
     * of that specific model.
     *
     * @param data  the provided data
     * @return      a List of (failures_count, model) pairs
     */
    public static List<Tuple2<Long, String>> query2First(JavaRDD<Tuple5<String, String, String, Long, Long>> data) {

        return data
                // Discard row without failures
                .filter(row -> row._4() != 0)
                // Map each row to (model, 1) pair
                .mapToPair(row -> new Tuple2<>(row._3(), 1L))
                // Sum failures count by model
                .reduceByKey(Long::sum)
                // Swap columns to take ordered results
                .mapToPair(row -> new Tuple2<>(row._2(), row._1()))
                // Sort by failures count
                .sortByKey(false)
                // Take TOP_N (10) models with more failures
                .take(TOP_N);
    }

    /**
     * Query 2_2
     * Next, calculate a second ranking of the 10 vaults with the highest number of failures.
     * For each vault, report the number of failures and the list (without repetitions)
     * of hark disk models subject to at least one failure.
     *
     * @param data  the provided data
     * @return      a List of (failures_count, (vault_id, models_list)) pairs
     */
    public static List<Tuple3<Long, Long, String>> query2Second(JavaRDD<Tuple5<String, String, String, Long, Long>> data) {

        //JavaPairRDD<Long, Tuple2<String, Long>> temp =
        return data
                // Discard row without failures
                .filter(row -> row._4() != 0)
                // Map each row to a (vault_id, (1, model)) pair
                .mapToPair(row -> new Tuple2<>(row._5(), new Tuple2<>(1L, new ArrayList<>(Collections.singletonList(row._3())))))
                // Sum failures count and concatenates models (unique) names
                .reduceByKey((Function2<Tuple2<Long, ArrayList<String>>, Tuple2<Long, ArrayList<String>>, Tuple2<Long, ArrayList<String>>>) (t1, t2) -> {
                    t1._2().addAll(t2._2());
                    return new Tuple2<>(t1._1() + t2._1(), new ArrayList<>(new HashSet<>(t1._2())));
                })
                // Map each row to a (failures_count, (vault_id, models_list)) pair
                .mapToPair(row -> {
                    StringBuilder sb = new StringBuilder();
                    row._2()._2().forEach(model -> sb.append(model).append(" "));
                    System.out.println(sb);
                    return new Tuple2<>(row._2()._1(), new Tuple2<>(row._1(), sb.toString()));
                })
                .sortByKey(false)
                .map(row -> new Tuple3<>(row._2()._1(), row._1(), row._2()._2()))
                .take(TOP_N);
    }
}
