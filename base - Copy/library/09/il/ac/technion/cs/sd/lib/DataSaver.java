package il.ac.technion.cs.sd.lib;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * the main class in our library
 */
public interface DataSaver {

    /**
     * Opens the database named db_name
     * @param db_name - the database's name
     * @throws DatabaseAlreadyOpen - if the database was already open.
     */
    public void openDatabase(String db_name) throws DatabaseAlreadyOpen;

    /**
     * Stores the data in the given data base (no order guaranteed)
     * @param db_name - the databases name
     * @param data - the data to store. in form of a map of keys and corresponding values
     * @throws NoSuchDatabaseOpen - If the database wasn't opened
     */
    public CompletableFuture<Void> storeData(String db_name, Map<String,String> data) throws NoSuchDatabaseOpen;

    /**
     * Stores the data in the given data base (order preserved)
     * @param db_name - the databases name
     * @param data - the data to store. in form of a list of keys and corresponding values
     * @throws NoSuchDatabaseOpen - If the database wasn't opened
     */
    public CompletableFuture<Void> storeData(String db_name, List<Map.Entry<String,String>> data) throws NoSuchDatabaseOpen;


    /**
     *
     * @param db_name - the databases name
     * @param key
     * @return the corresponding value to the key under the database named db_name
     * @throws NoSuchDatabaseOpen - If the database wasn't opened
     */
    public CompletableFuture<String> getData(String db_name, String key) throws NoSuchDatabaseOpen, InterruptedException;

    /**
     *
     * @param db_name
     * @param startKey
     * @return an iterator over the given database starting from the given key
     * @throws NoSuchDatabaseOpen - If the database wasn't opened
     */
    public CompletableFuture<LibIterator> iterator(String db_name, String startKey) throws  NoSuchDatabaseOpen;

    /**
     *
     * @param db_name
     * @return an iterator over all the given database
     * @throws NoSuchDatabaseOpen
     */
    public CompletableFuture<LibIterator> iterator(String db_name) throws  NoSuchDatabaseOpen;

}
