package il.ac.technion.cs.sd.lib;


import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

public class InnerDataSaverClass implements InnerDataSaver {

    private CompletableFuture<FutureSecureDatabase> db;
    private String firstKey;
    private String lastKey;
    private String lastData;
    private String separator;

    public InnerDataSaverClass(CompletableFuture<FutureSecureDatabase> db, String separator) {

        this.db = db;
        this.separator = separator;
        this.firstKey = ",";
        this.lastData = "";
        this.lastKey = "";
    }

    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture[com.size()]))
                .thenApply(v -> com.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
    }

    @Override
    public CompletableFuture<LibIterator> getIterator() {
        return getIterator(firstKey);
    }

    @Override
    public CompletableFuture<LibIterator> getIterator(String key) {
        Function<FutureSecureDatabase, CompletionStage<byte[]>> checkKey = fdb -> fdb.get(key.getBytes());
        BiFunction<byte[], Throwable, LibIteratorClass> createIterator = (ans, err) ->
                (ans != null) ? new LibIteratorClass(key, db, separator) : new LibIteratorClass("", db, separator);
        return db.thenCompose(checkKey).handle(createIterator);
    }

    @Override
    public CompletableFuture<String> getData(String key) {

        Function<byte[], CompletionStage<String>> byteToString =
                y -> CompletableFuture.completedFuture(new String(y));
        Function<String, CompletionStage<Integer>> getLength =
                x -> CompletableFuture.completedFuture(Integer.parseInt(x.split(separator)[0]));
        Function<List<String>, CompletionStage<String>> combineStrings =
                l -> CompletableFuture.completedFuture(l.stream().collect(Collectors.joining()));
        Function<? super FutureSecureDatabase, CompletionStage<byte[]>> getKey =
                fdb -> fdb.get(key.getBytes());
        BiFunction<Integer, ? super FutureSecureDatabase, CompletableFuture<List<String>>> getAllParts =
                (l, fdb) -> {
                    List<CompletableFuture<String>> parts = new ArrayList<>();
                    for (int i = 0; i < l; i++) {
                        parts.add(fdb.get((key + separator + i).getBytes()).thenCompose(byteToString));
                    }
                    return sequence(parts);
                };
        Function<CompletableFuture<List<String>>, CompletableFuture<List<String>>> id = (x) -> x;
        return db.thenCompose(getKey).thenCompose(byteToString).thenCompose(getLength).thenCombine(db, getAllParts)
                .thenCompose(id).thenCompose(combineStrings);
    }

    private void storeData(FutureSecureDatabase fdb, String key, String data) {
        byte[] data_b = data.getBytes();
        int len = data_b.length / 100;
        if (data_b.length % 100 > 0) {
            len++;
        }
        String tmp = len + separator;
        try {
            CompletableFuture<Void> temp = fdb.addEntry(key.getBytes(), tmp.getBytes());
            temp.get();
        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < len; i++) {
            try {
                fdb.addEntry((key + separator + i).getBytes(), Arrays.copyOfRange(data_b, i * 100,
                        Math.min((i + 1) * 100, data_b.length))).get();
            } catch (DataFormatException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (lastKey.equals("")) {
            firstKey = key;
            try {
                fdb.addEntry(separator.getBytes(), (separator + key).getBytes()).get();
            } catch (DataFormatException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            tmp = lastData + key;
            try {
                fdb.addEntry(lastKey.getBytes(), tmp.getBytes()).get();
            } catch (DataFormatException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        lastKey = key;
        lastData = len + separator;
    }

    @Override
    public CompletableFuture<Void> storeData(Map<String, String> data) {
        return db.thenCompose(d -> storeData(d, data));
    }

    private CompletionStage<Void> storeData(FutureSecureDatabase d, Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            storeData(d, key, value);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> storeData(List<Map.Entry<String, String>> data) {
        return db.thenCompose(d -> storeData(d, data));
    }

    private CompletionStage<Void> storeData(FutureSecureDatabase d, List<Map.Entry<String, String>> data) {
        for (Map.Entry<String, String> entry : data) {
            String key = entry.getKey();
            String value = entry.getValue();
            storeData(d, key, value);
        }
        return CompletableFuture.completedFuture(null);
    }

}
