package lib;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.zip.DataFormatException;

public interface FreeBeerDB extends FutureSecureDatabase {

    CompletableFuture<Void> addEntry(byte[] key, byte[] value) throws DataFormatException;

    CompletableFuture<byte[]> get(byte[] key);

    CompletableFuture<Boolean> contains(byte[] key);

    CompletableFuture<List<ByteBuffer>> keys();

    CompletableFuture<List<ByteBuffer>> values();

    CompletableFuture<Map<ByteBuffer, ByteBuffer>> entries();

    CompletableFuture<List<ByteBuffer>> sortedValues(Comparator<ByteBuffer> cmp);

    CompletableFuture<List<ByteBuffer>> sortedKeys(Comparator<ByteBuffer> cmp);

    CompletableFuture<Optional<ByteBuffer>> maxValue(Comparator<ByteBuffer> cmp);

}