package lib;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class FreeBeerDBFactoryImpl implements FreeBeerDBFactory {
    private HashMap<String, FreeBeerDB> databases;
    private FutureSecureDatabaseFactory factory;

    @Inject
    public FreeBeerDBFactoryImpl(FutureSecureDatabaseFactory factory) {
        this.databases = new HashMap<>();
        this.factory = factory;
    }

    @Override
    public CompletableFuture<FreeBeerDB> open(String s) {
        databases.putIfAbsent(s, new FreeBeerDBImpl(s, factory));
        return CompletableFuture.supplyAsync(() -> databases.get(s));
    }

}
