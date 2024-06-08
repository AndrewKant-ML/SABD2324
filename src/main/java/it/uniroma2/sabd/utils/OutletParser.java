package it.uniroma2.sabd.utils;

public class OutletParser {

    public static DataRow parseCSV(String csvLine) {

        String[] csvValues = csvLine.split(",");

        if (csvValues[3].equals("failure"))
            return null;

        return new DataRow(
                csvValues[0], // timestamp
                csvValues[1], // serial number
                csvValues[2], // model
                Long.parseLong(csvValues[3]), // failure
                Long.parseLong(csvValues[4]) // vault id
        );
    }
}




