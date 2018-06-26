package MyDatabase;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MyFutureSecureDatabaseFactory implements FutureSecureDatabaseFactory {
    private final Map<String, FutureSecureDatabase> dbs = new HashMap<>();

    // this really should have thrown an interrupted exception :| oh well
    @Override
    public CompletableFuture<FutureSecureDatabase> open(String dbName) {
        try {
            Thread.sleep(dbs.size() * 100);
            if (!dbs.containsKey(dbName))
                dbs.put(dbName, new MyFutureSecureDatabase());
            return CompletableFuture.completedFuture(dbs.get(dbName));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
