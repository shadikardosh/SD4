package il.ac.technion.cs.sd.lib;


import com.google.inject.Inject;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DataSaverClass implements DataSaver{

    FutureSecureDatabaseFactory factory;
    Map<String, InnerDataSaver> dbs = new HashMap<>();
    String separator;


    @Inject
    public DataSaverClass(FutureSecureDatabaseFactory factory){
        this(factory, ",");
    }


    public DataSaverClass(FutureSecureDatabaseFactory factory, String separator){
        this.separator = separator;
        this.factory = factory;
    }

    @Override
    public void openDatabase(String db_name) throws DatabaseAlreadyOpen {
        if(dbs.containsKey(db_name)){
            throw new DatabaseAlreadyOpen();
        }
        else {
            CompletableFuture<FutureSecureDatabase> db = factory.open(db_name);
            InnerDataSaver dbHandler = new InnerDataSaverClass(db,separator);
            dbs.put(db_name, dbHandler);
        }
    }

    @Override
    public CompletableFuture<Void> storeData(String db_name, Map<String, String> data) throws NoSuchDatabaseOpen {
        if(dbs.containsKey(db_name)){
            return dbs.get(db_name).storeData(data);
        }
        else {
            throw new NoSuchDatabaseOpen();
        }
    }

    @Override
    public CompletableFuture<Void> storeData(String db_name, List<Map.Entry<String, String>> data) throws NoSuchDatabaseOpen {
        if(dbs.containsKey(db_name)){
            return dbs.get(db_name).storeData(data);
        }
        else {
            throw new NoSuchDatabaseOpen();
        }
    }


    @Override
    public CompletableFuture<String> getData(String db_name, String key) throws NoSuchDatabaseOpen, InterruptedException {
        if(dbs.containsKey(db_name)){
            return dbs.get(db_name).getData(key);
        }
        else {
            throw new NoSuchDatabaseOpen();
        }
    }

    @Override
    public CompletableFuture<LibIterator> iterator(String db_name, String startKey) throws NoSuchDatabaseOpen {
        if(dbs.containsKey(db_name)){
            return dbs.get(db_name).getIterator(startKey);
        }
        else {
            throw new NoSuchDatabaseOpen();
        }
    }

    @Override
    public CompletableFuture<LibIterator> iterator(String db_name) throws NoSuchDatabaseOpen {
        if(dbs.containsKey(db_name)){
            return dbs.get(db_name).getIterator();
        }
        else {
            throw new NoSuchDatabaseOpen();
        }
    }
}
