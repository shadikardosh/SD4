package lib;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

public class FreeBeerDBImpl implements FreeBeerDB {
    private FutureSecureDatabase db_index;    // maps 0,1,2,3,...->keys. Needed to allow us to get all keys from db.
    private FutureSecureDatabase db_chunks_per_value;  // maps key->number of chunks (typically 100B each) in value.
    private FutureSecureDatabase db_data; // maps key(1)->value chunks. See note below.
    private FutureSecureDatabase db_metadata; // See enum MetadataKey
    /*
    * (1) Note on keys: first 4 bytes are index of chunk (0,1,2... n_chunks_per_value), the rest is the key itself.
    * Example: suppose, user calls addEntry('a_key'.bytes(), 'some_very_big_value, 200 bytes long ... ... end'.bytes()),
    * chunk size is 100 bytes, and DB had 3 entries before.
    *
    * db_index will store 3->'a_key'
    * db_chunks_per_value will store 'a_key'->2
    * db_data will store 0003'a_key'->'some_very_big_value, 200 bytes long ...'
    *                    0003'a_key'->' ... end'
    * db_metadata will store DB_SIZE->4
    */
    enum MetadataKey {
        DB_SIZE; // number of keys stored in DB (number of values is larger, because we break each value into chunks)

        byte[] getBytes() {
            return int2Bytes(ordinal());
        }
    }

    private int db_size; // max index in db_index, cached from actual db_metadata (possibly) on disk

    private String name; // not necessary, but can help with debugging

    private final static int BYTES_IN_CHUNK = 100; // That's maximal size of value FutureSecureDatabase can store

    @Inject
    public FreeBeerDBImpl(String name, FutureSecureDatabaseFactory dbFactory) {
        /*
         * Note: we must use <name> when opening SecureDatabase,
         * otherwise each instance of FreeBeerDB will use the same SecureDatabases: _index, _data, ...
         */
        if (!name.equals("")) {
            this.name = name;
        } else {
            /* This is mostly for unit tests, when creating FreeBeerDB with Guice - it creates with name="",
             * Which can cause names collision - 2 instances of FreBeerDB will run over same SecureDBs */
            this.name = Integer.toHexString(1024*1024 + new Random().nextInt(1024* 1024 * 1024));
        }

        CompletableFuture<Void> create_all_dbs = CompletableFuture.allOf(
                dbFactory.open(this.name + "_index").thenAccept(db -> this.db_index = db),
                dbFactory.open(this.name + "_key_to_size").thenAccept(db -> this.db_chunks_per_value = db),
                dbFactory.open(this.name + "_data").thenAccept(db -> this.db_data = db),
                dbFactory.open(this.name + "_metadata").thenAccept(db -> this.db_metadata = db)
        );

        create_all_dbs.thenCompose(unused -> syncDbSize())
                .thenApply(db_size -> this.db_size = db_size)
                .join();
    }

    /**
     * Note: this method is defined as synchronized to ensure there are no concurrent writes to DB,
     * as we have been warned in assignment.
     */
    @Override
    public synchronized CompletableFuture<Void> addEntry(byte[] key, byte[] value) throws DataFormatException {
        int db_size_before = this.db_size; // store old value to avoid race condition between addEntry and incDbSize
        int n_chunks_per_value = (int) Math.ceil((double) value.length / BYTES_IN_CHUNK);

        List<CompletableFuture<Void>> requests = new ArrayList<>();
        requests.add(db_index.addEntry(int2Bytes(db_size_before), key));
        requests.add(db_chunks_per_value.addEntry(key, int2Bytes(n_chunks_per_value)));
        requests.add(incDbSize().thenAccept(new_size -> this.db_size = new_size));
        // break value to chunks and prepare addEntry requests for all chunks:
        for (int i=0; i < n_chunks_per_value; i++) {
            byte[] chunk = Arrays.copyOfRange(value, i * BYTES_IN_CHUNK, Math.min((i + 1) * BYTES_IN_CHUNK, value.length));
            requests.add(db_data.addEntry(prependIndex(i, key), chunk));
        }

        return CompletableFuture.allOf(requests.toArray(new CompletableFuture[requests.size()]));
    }

    @Override
    public CompletableFuture<byte[]> get(byte[] key) {
        List<CompletableFuture<byte[]>> chunk_requests = new ArrayList<>();

        CompletableFuture<Void> fetch_all_chunks = db_chunks_per_value.get(key).thenCompose(n_chunks -> {
            for (int i = 0; i < ByteBuffer.wrap(n_chunks).getInt(); i++) {
                chunk_requests.add(db_data.get(prependIndex(i, key)));
            }
            return CompletableFuture.allOf(chunk_requests.toArray(new CompletableFuture[chunk_requests.size()]));
        });

        return fetch_all_chunks.thenApply(unused ->
                chunk_requests.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        ).thenApply(FreeBeerDBImpl::assembleValue);
    }

    @Override
    public CompletableFuture<Boolean> contains(byte[] key) {
        try {
            return db_data.get(prependIndex(0, key)).thenApply(unused -> true);
        } catch (NoSuchElementException e) {
            return CompletableFuture.supplyAsync(()->false);
        }
    }

    @Override
    public CompletableFuture<List<ByteBuffer>> keys() {
        List<CompletableFuture<byte[]>> keys_requests = getAllkeysRequestList();

        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(keys_requests.toArray(new CompletableFuture[keys_requests.size()]));

        // This converts List<CompletableFuture<byte[]>> to CompletableFuture<List<ByteBuffer>>
        return allFutures.thenApply(unused ->
            keys_requests.stream()
                    .map(CompletableFuture::join)
                    .map(ByteBuffer::wrap)
                    .collect(Collectors.toList())
        );
    }

    @Override
    public CompletableFuture<List<ByteBuffer>> values() {
        List<CompletableFuture<byte[]>> values_requests = getAllkeysRequestList().stream()
                .map(CompletableFuture::join)
                .map(key -> get(key))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture
                .allOf(values_requests.toArray(new CompletableFuture[values_requests.size()]));

        // This converts List<CompletableFuture<byte[]>> to CompletableFuture<List<ByteBuffer>>
        return allFutures.thenApply(unused ->
                values_requests.stream()
                        .map(CompletableFuture::join)
                        .map(ByteBuffer::wrap)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Internally we prefer to work with list of Futures instead of Future of list
     */
    private List<CompletableFuture<byte[]>> getAllkeysRequestList() {
        List<CompletableFuture<byte[]>> keys_requests = new ArrayList<>();
        for (int i = 0; i< this.db_size; i++) {
            keys_requests.add(db_index.get(int2Bytes(i)));
        }
        return keys_requests;
    }

    @Override
    public CompletableFuture<Map<ByteBuffer, ByteBuffer>> entries() {
        Map<byte[], CompletableFuture<byte[]>> keys_and_values_requests = getAllkeysRequestList().stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(
                        key -> key,
                        key -> get(key)
                ));

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(keys_and_values_requests.values()
                .toArray(new CompletableFuture[keys_and_values_requests.size()]));

        return allFutures.thenApply(unused ->
                keys_and_values_requests.entrySet().stream()
                    .collect(Collectors.toMap(
                            kv -> ByteBuffer.wrap(kv.getKey()),
                            kv -> ByteBuffer.wrap(kv.getValue().join())
                        ))
                );
    }

    @Override
    public CompletableFuture<List<ByteBuffer>> sortedValues(Comparator<ByteBuffer> cmp) {
        return  values().thenApply(
                    values_list -> values_list.stream().sorted(cmp).collect(Collectors.toCollection(ArrayList::new))
                );
    }

    @Override
    public CompletableFuture<List<ByteBuffer>> sortedKeys(Comparator<ByteBuffer> cmp) {
        return  keys().thenApply(
                keys_list -> keys_list.stream().sorted(cmp).collect(Collectors.toCollection(ArrayList::new))
            );
    }

    @Override
    public CompletableFuture<Optional<ByteBuffer>> maxValue(Comparator<ByteBuffer> cmp) {
        return values().thenApply(
                values_list -> values_list.stream().max(cmp)
        );
    }

    /**
     * Since we must support persistent storage, we need to synchronize between this.db_size (in-memory) and
     * actual DB size (on disk), upon creating FreeBeerDB instance (when opening an existing DB).
     * @return updated DB size
     */
    private CompletableFuture<Integer> syncDbSize() {
        try {
            // When creating FreeBeer instance over existing DB on disk - read DB size from disk:
            return db_metadata.get(MetadataKey.DB_SIZE.getBytes())
                    .thenApply(bytes -> ByteBuffer.wrap(bytes).getInt());

        } catch (NoSuchElementException e) {
            // When creating a new FreeBeer instance - write (DB size = 0) to disk:
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return db_metadata.addEntry(MetadataKey.DB_SIZE.getBytes(), int2Bytes(0));
                } catch (DataFormatException e1) {
                    throw new AssertionError("Unexpected: DB value size > 100");
                }
            }).thenApply(unused -> 0);
        }
    }

    /**
     * Since we must support persistent storage, we need to store db_size in DB upon every insertion,
     * so we will be able to retrieve it later.
     * @return updated DB size
     */
    private CompletableFuture<Integer> incDbSize() {
        int new_index = this.db_size + 1;
        try {
            return db_metadata.addEntry(MetadataKey.DB_SIZE.getBytes(), int2Bytes(new_index))
                    .thenApply(unused -> new_index);
        } catch (DataFormatException e) {
            throw new AssertionError("Unexpected: DB value size > 100");
        }
    }

    private static byte[] int2Bytes(int i) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(i).array();
    }

    private static byte[] prependIndex(int index, byte[] key) {
        return ArrayUtils.addAll(int2Bytes(index), key);
    }

    private static byte[] assembleValue(List<byte[]> chunks) {
        ByteBuffer buffer = ByteBuffer.allocate(BYTES_IN_CHUNK * (chunks.size()-1) + chunks.get(chunks.size()-1).length);
        chunks.forEach(buffer::put);
        return buffer.array();
    }
}
