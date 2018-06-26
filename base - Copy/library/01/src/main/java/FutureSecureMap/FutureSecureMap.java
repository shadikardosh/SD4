package FutureSecureMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * An interface for a map from string key to a given class value.
 *
 * @param <V> the class of a the values to be stored. //TODO: need to be updated
 */
public interface FutureSecureMap<K,V> {

    /**
     * Insert a new value with a given key. If the already exists, override its value.
     *
     * @param key   the key.
     * @param value the value to be stored.
     */
    CompletableFuture<Void> addEntry(K key, V value);

    /**
     * Returns the value corresponding with a given key.
     *
     * @param key the key.
     * @return the corresponding value with the  key, if exists.
     * @throws java.util.NoSuchElementException if the given key doesn't exists.
     */
    CompletableFuture<V> get(K key);


    /**
     * @param map
     * @return
     */
    CompletableFuture<Void> addFromMap(Map<K, V> map);

}
