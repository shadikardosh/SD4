package lib;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.DataFormatException;

public class FutureDatabaseFake implements FutureSecureDatabase {

    private HashMap<ByteBuffer, ByteBuffer> dict;

    public FutureDatabaseFake(){
        dict = new HashMap<>();
    }

    @Override
    public CompletableFuture<Void> addEntry(byte[] key, byte[] value) throws DataFormatException {
        if (value.length > 100)
            throw new DataFormatException();
        dict.put(ByteBuffer.wrap(key), ByteBuffer.wrap(value));
        return CompletableFuture.supplyAsync(() -> null);
    }

    @Override
    public CompletableFuture<byte[]> get(byte[] key) {
        if (!dict.containsKey(ByteBuffer.wrap(key))){
            throw new NoSuchElementException();
        }
        return CompletableFuture.supplyAsync(() -> dict.get(ByteBuffer.wrap(key)).array());
    }
}
