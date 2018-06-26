package StringDataBase;

import DataBase.DataBase;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

/**
 * A map form string key to string value, which enabling storing of along string as values.
 */
public class DataBaseForLongString implements StringDataBase {
    private DataBase db;


    @Inject
    DataBaseForLongString(DataBase db) {
        this.db = db;
    }

    @Override
    public CompletableFuture<Void> addEntry(String key, String value) {
        return db.addEntry(key.getBytes(), value.getBytes());
    }

    @Override
    public CompletableFuture<String> get(String key) {
        return db.get(key.getBytes()).thenApply(String::new);
    }
}
