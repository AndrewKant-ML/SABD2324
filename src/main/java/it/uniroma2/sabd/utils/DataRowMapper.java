package it.uniroma2.sabd.utils;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;

public class DataRowMapper implements MapFunction<Row, DataRow> {
    @Override
    public DataRow call(Row row) throws Exception {
        return new DataRow(
                row.getString(0),   // timestamp
                row.getString(1),   // serial number
                row.getString(2),   // model
                row.getLong(3),     // failure
                row.getLong(4)      // vault id
        );
    }
}
