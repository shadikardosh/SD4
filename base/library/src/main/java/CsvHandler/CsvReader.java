package main.java.CsvHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;


public class CsvReader {
    private BufferedReader reader;

    public CsvReader(BufferedReader reader) {
        this.reader = reader;
    }

    public CsvReader(String csvData) {
        this(new BufferedReader(new StringReader(csvData)));
    }

    /**
     * Reads a line of csv db.
     *
     * @return     Array of strings representing the entry described in the csv line
     *             if there are still lines to read. Else, null.
     *
     * @exception  IOException  If an I/O error occurs
     *
     */
    public String[] readLine() {
        /* if input is already exhausted */
        if (reader == null) return null;

        String line;
        String[] data = null;

        try {
            line = reader.readLine();

            if (line == null) {
                reader.close();
                reader = null;
                return null;
            }

            data = line.split(",");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
