package Library;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.zip.DataFormatException;

public class Library implements LibraryInterface {

    private FutureSecureDatabaseFactory databaseFactory;
    private Map<String,CompletableFuture<FutureSecureDatabase>> databaseMap;

    @Inject
    public Library(FutureSecureDatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
        databaseMap = new HashMap<>();
    }

    public CompletableFuture<Void> addEntry(String dataBaseName, String key, String value)  throws LibraryException{
        try {
            if (!databaseMap.containsKey(dataBaseName)) {
                databaseMap.put(dataBaseName, databaseFactory.open(dataBaseName));
            }
            CompletableFuture<FutureSecureDatabase> db = databaseMap.get(dataBaseName);
            return CompletableFuture.completedFuture(db.get().addEntry(key.getBytes(), value.getBytes()).join());
        }catch (DataFormatException e) {
            throw new ValueTooLongException();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public CompletableFuture<Optional<String>> get(String dataBaseName, String key) throws LibraryException{
        try {
            if(!databaseMap.containsKey(dataBaseName)) {
                databaseMap.put(dataBaseName, databaseFactory.open(dataBaseName));
            }
            CompletableFuture<FutureSecureDatabase> db = databaseMap.get(dataBaseName);
            return db.thenCompose(s -> s.get(key.getBytes())).exceptionally(e -> new byte[0]).thenApply(str -> Optional.ofNullable(new String(str)).filter(st -> !st.isEmpty())).exceptionally(ex-> Optional.empty());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
