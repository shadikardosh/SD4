package il.ac.technion.cs.sd.lib;


import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * wraps a specific database from the ones opened with the main library DataSaver
 */

public interface InnerDataSaver {

        /**
         *
         * @param key
         * @return the corresponding value to the key under the current database
         */
        public CompletableFuture<String> getData(String key);

        /**
         *
         * @param key
         * @return an iterator over the current database starting from the given key
         */
        public CompletableFuture<LibIterator> getIterator(String key);

        /**
         *
         * @return an iterator over all the current database
         */
        public CompletableFuture<LibIterator> getIterator();

        /**
         * Stores the data in the current data base (no order guaranteed)
         * @param data - the data to store. in form of a map of keys and corresponding values
         */
        public CompletableFuture<Void> storeData(Map<String,String> data);

        /**
         * Stores the data in the current data base (order preserved)
         * @param data - the data to store. in form of a list of keys and corresponding values
         */
        public CompletableFuture<Void> storeData(List<Map.Entry<String,String>> data);

}

