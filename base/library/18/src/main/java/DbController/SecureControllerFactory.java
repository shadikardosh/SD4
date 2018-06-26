package DbController;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

import java.util.concurrent.ExecutionException;

public class SecureControllerFactory implements ControllerFactory {
    private final FutureSecureDatabaseFactory secureDbFactory;

    @Inject
    public SecureControllerFactory(FutureSecureDatabaseFactory secureDbFactory) {
        this.secureDbFactory = secureDbFactory;
    }


    public Controller open(String dbName) {
        try {
            return new SecureController(secureDbFactory.open(dbName).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
