package lib;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class FutureDataBaseFactoryFake implements FutureSecureDatabaseFactory {
    private HashMap<String, FutureSecureDatabase> databases;

    public FutureDataBaseFactoryFake(){
        databases = new HashMap<>();
    }

    @Override
    public CompletableFuture<FutureSecureDatabase> open(String s) {
        databases.putIfAbsent(s, new FutureDatabaseFake());
        return CompletableFuture.supplyAsync(() -> databases.get(s));
    }
}
