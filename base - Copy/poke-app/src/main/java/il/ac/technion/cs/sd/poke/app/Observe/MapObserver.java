package il.ac.technion.cs.sd.poke.app.Observe;

import DbController.Controller;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface MapObserver {

    public void initDB(Controller ctrl) throws InterruptedException, ExecutionException;
    void notify(MapObservable mo);
}
