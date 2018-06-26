package MyDatabase;
//import il.ac.technion.cs.sd.pay.ext.SecureDatabase;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.zip.DataFormatException;

class MyFutureSecureDatabase implements FutureSecureDatabase {
    Map<String, String> data;
    MyFutureSecureDatabase() {
        data = new HashMap<>();
    }

    public CompletableFuture<Void> addEntry(byte[] key, byte[] value) throws DataFormatException {
        data.put(new String(key, StandardCharsets.UTF_8), new String(value, StandardCharsets.UTF_8));
        //return CompletableFuture.completedFuture(Void);
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<byte[]> get(byte[] key) {
        if (data.get(new String(key, StandardCharsets.UTF_8)) == null)
            throw new NoSuchElementException();

        String res = data.get(new String(key, StandardCharsets.UTF_8));
        try {
            Thread.sleep(res.length());
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture(data.get(new String(key, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
    }
}
