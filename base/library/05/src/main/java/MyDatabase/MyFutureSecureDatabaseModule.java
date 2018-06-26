package MyDatabase;

import com.google.inject.AbstractModule;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//import il.ac.technion.cs.sd.pay.ext.SecureDatabaseFactory;

public class MyFutureSecureDatabaseModule extends AbstractModule {
    Map<String, CompletableFuture<FutureSecureDatabase>> DatabasesByName;
    public MyFutureSecureDatabaseModule() {
        DatabasesByName = new HashMap<>();
    }

    /*protected void configure() {
        this.bind(FutureSecureDatabaseFactory.class).toInstance((unused) -> {
            return CompletableFuture.completedFuture(new MyFutureSecureDatabase());
        });
    }*/

    protected void configure() {
        this.bind(FutureSecureDatabaseFactory.class).toInstance((String s) -> {
            if (DatabasesByName.containsKey(s)) {
                CompletableFuture<FutureSecureDatabase> res = CompletableFuture.completedFuture(DatabasesByName.get(s).join());
                return res;
            }

            try {
                Thread.sleep(100);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            CompletableFuture<FutureSecureDatabase> res = CompletableFuture.completedFuture(new MyFutureSecureDatabase());
            DatabasesByName.put(s, res);
            return res;
        });
    }
}
