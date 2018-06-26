package StringDataBase;

import java.util.concurrent.CompletableFuture;

/**
 * An interface for databases mapping from string key to a string value.
 * Supporting addEntry and get actions.
 */
public interface StringDataBase {

    /**
     * Inserts a given value with a given key.
     * If the already exists, overrides its value.
     *
     * @param key   key string
     * @param value the value corresponding with the key.
     */
    CompletableFuture<Void> addEntry(String key, String value);

    /**
     * Returns the value corresponding with a given key.
     *
     * @param key key string.
     * @return the value corresponding with the key, if exists.
     * @throws java.util.NoSuchElementException if the given key doesn't exist.
     * @throws InterruptedException
     */
    CompletableFuture<String> get(String key);
}
