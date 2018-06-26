package il.ac.technion.cs.sd.lib;

import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * Uses for iteration over the data saved in secureDataBase
 */
public interface LibIterator {

    /**
     *
     * @return CompletableFuture<Map.Entry<String,String>> containing the <key,value>
     */
    public CompletableFuture<Map.Entry<String,String>> next();

    /**
     *
     * @return CompletableFuture<Boolean> returns true if there is more data to iterate over
     */
    public CompletableFuture<Boolean> hasNext();

}
