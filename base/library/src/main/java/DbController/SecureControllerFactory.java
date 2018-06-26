package main.java.DbController;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.poke.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.poke.ext.FutureSecureDatabaseFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class SecureControllerFactory implements ControllerFactory {
    private final FutureSecureDatabaseFactory secureDbFactory;

    @Inject
    public SecureControllerFactory(FutureSecureDatabaseFactory secureDbFactory) {
        this.secureDbFactory = secureDbFactory;
    }


    public Controller open(String dbName) {
        try {
            Optional<FutureSecureDatabase> ret = secureDbFactory.open(dbName).get();
            if(ret.isPresent()) return new SecureController(ret.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
