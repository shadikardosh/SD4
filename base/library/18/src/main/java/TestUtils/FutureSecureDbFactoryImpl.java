package TestUtils;

import il.ac.technion.cs.sd.movie.ext.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

public class FutureSecureDbFactoryImpl implements FutureSecureDatabaseFactory {
    private static class FutureSecureDatabaseImpl implements FutureSecureDatabase {
        private final Map<String, String> map = new HashMap<>();

        /** Adds a new security entry. If the key already exists then it is replaced. */
        public CompletableFuture<Void> addEntry(byte[] key, byte[] value) throws DataFormatException {
            if(value.length > 100)
                throw new DataFormatException();
            map.put(new String(key), new String(value));
            return null;
        }

        /**
         * Returns the contents of the entry that was stored with the key k.
         * @throws java.util.NoSuchElementException If the key doesn't exist.
         */
        public CompletableFuture<byte[]> get(byte[] key) {
            String strValue = map.get(new String(key));

            if(null == strValue)
                throw new NoSuchElementException();
            byte[] value = strValue.getBytes();
            try {
                Thread.sleep(value.length);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return CompletableFuture.completedFuture(value);
        }
    }

    private final Map<String, FutureSecureDatabase> dbs = new HashMap<>();

    // this really should have thrown an interrupted exception :| oh well
    @Override
    public CompletableFuture<FutureSecureDatabase> open(String dbName) {
        try {
            Thread.sleep(dbs.size() * 100);
            if (!dbs.containsKey(dbName))
                dbs.put(dbName, new FutureSecureDatabaseImpl());
            return CompletableFuture.completedFuture(dbs.get(dbName));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
