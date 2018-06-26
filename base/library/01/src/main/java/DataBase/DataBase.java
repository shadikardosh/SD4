package DataBase;


import java.util.concurrent.CompletableFuture;

/**
 * An interface for databases supporting addEntry and get actions.
 */
public interface DataBase {

    /**
     * Adds a new value into the database with a given key.
     * if the key already exists, overrides the old value.
     *
     * @param key   the key as byte array.
     * @param value the value as byte array.
     */
    CompletableFuture<Void> addEntry(byte[] key, byte[] value);


    /**
     * Returns the value for a given key.
     *
     * @param key the key as byte array.
     * @return the corresponding value for the key, as a byte array.
     * @throws java.util.NoSuchElementException in case the given key isn't present.
     * @throws InterruptedException
     */
    CompletableFuture<byte[]> get(byte[] key);
}
