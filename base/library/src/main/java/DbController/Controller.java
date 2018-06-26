package main.java.DbController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The main handler of SecureDatabase.
 */

public interface Controller {

    /**
     * Accepts a String in format of csv (each line contains "<key>, <value>"), and inserts the data to the db.
     */
    CompletableFuture<Void> insertDataFromCsv(String csvData);

    /**
     * Inserts the map to the database
     */
    CompletableFuture<Void> insertData(Map<String, String> data);

    /**
     * Adds a simple entry to the database
     */
    CompletableFuture<Boolean> insertEntry(String key, String value);

    /**
     * Adds the list to the database with the specified key.
     */
    CompletableFuture<Void> dumpList(String key, List<String> data);

    /**
     * Adds the map to the database with the specified key.
     */
    CompletableFuture<Void> dumpMapOfLists(String key, Map<String, List<String>> data);

    CompletableFuture<String> getValue(Integer key);

    CompletableFuture<String> getValue(String key);

    /**
     * Returns the list which was added to the db with dumpList method.
     */
    CompletableFuture<List<String>> getList(String key);

    /**
     * Returns the map which was added to the db with dumpList method.
     */
    CompletableFuture<Map<String, List<String>>> getMapOfLists(String initialKey);

    /**
     * Returns a list values corresponding to the specified keys.
     */
    CompletableFuture<List<String>> getValuesCorrespondingTo(List<String> keys);
}
