package FutureSecureMap;

import StringDataBase.StringDataBase;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class FutureSecureMapImpl<K, V> implements FutureSecureMap<K, V> {

    private final StringDataBase db;

    private final Function<K, String> keyToString;
    private final Function<V, String> valueToString;
    private final Function<String, V> valueFromString;

    public FutureSecureMapImpl(StringDataBase db, Function<K, String> keyToString,
                               Function<V, String> valueToString, Function<String, V> valueFromString) {
        this.db = db;
        this.keyToString = keyToString;
        this.valueToString = valueToString;
        this.valueFromString = valueFromString;
    }

    @Override
    public CompletableFuture<Void> addEntry(K key, V value) {
        return db.addEntry(keyToString.apply(key), valueToString.apply(value));
    }

    @Override
    public CompletableFuture<V> get(K key) {
        return db.get(keyToString.apply(key)).thenApply(valueFromString);
    }

    @Override
    public CompletableFuture<Void> addFromMap(Map<K, V> map) {
        CompletableFuture<Void> ret = CompletableFuture.completedFuture(null);
        for (Map.Entry<K, V> entry : map.entrySet()) {
            ret = CompletableFuture.allOf(ret, addEntry(entry.getKey(), entry.getValue()));
        }
        return ret;
    }
}
