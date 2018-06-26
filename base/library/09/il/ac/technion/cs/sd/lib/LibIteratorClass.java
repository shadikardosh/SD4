package il.ac.technion.cs.sd.lib;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

public class LibIteratorClass implements LibIterator {

    private CompletableFuture<String> currentKey;
    private CompletableFuture<FutureSecureDatabase> db;
    private String separator;


    LibIteratorClass(String firstKey, CompletableFuture<FutureSecureDatabase> db, String separator) {
        this.currentKey = CompletableFuture.completedFuture(firstKey);
        this.db = db;
        this.separator = separator;
    }

    private static<T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> com) {
        return CompletableFuture.allOf(com.toArray(new CompletableFuture[com.size()]))
                .thenApply(v -> com.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
    }

    private CompletableFuture<String> getKey(String key){
        if (key == ""){
            return CompletableFuture.completedFuture("");
        }
        Function<byte[], CompletionStage<String>> byteToString =
                y-> CompletableFuture.completedFuture(new String(y));
        Function<String, CompletionStage<Integer>> getLength =
                x -> CompletableFuture.completedFuture(Integer.parseInt(x.split(separator)[0]));
        Function<List<String>, CompletionStage<String>> combineStrings =
                l -> CompletableFuture.completedFuture(l.stream().collect(Collectors.joining()));
        Function<? super FutureSecureDatabase, CompletionStage<byte[]>> getKey =
                fdb -> fdb.get(key.getBytes());
        BiFunction<Integer, ? super FutureSecureDatabase, CompletableFuture<List<String>>> getAllParts =
                (l,fdb) -> {
                    List<CompletableFuture<String>> parts = new ArrayList<>();
                    for (int i = 0; i < l; i++){
                        parts.add(fdb.get((key + separator + i).getBytes()).thenCompose(byteToString));
                    }
                    return sequence(parts);
                };
        Function<CompletableFuture<List<String>>,CompletableFuture<List<String>>> id = (x)->x;
        return db.thenCompose(getKey).thenCompose(byteToString).thenCompose(getLength).thenCombine(db, getAllParts)
                .thenCompose(id).thenCompose(combineStrings);
    }

    private CompletableFuture<String> getNextKey(String key){
        if (key == ""){
            return CompletableFuture.completedFuture("");
        }
        Function<byte[], CompletionStage<String>> byteToString =
                y-> CompletableFuture.completedFuture(new String(y));
        Function<String, CompletionStage<String>> nextKey =
                x -> (x.split(separator).length == 2) ?
                        CompletableFuture.completedFuture(x.split(separator)[1]) :
                        CompletableFuture.completedFuture("");
        Function<? super FutureSecureDatabase, CompletionStage<byte[]>> getKey =
                fdb -> fdb.get(key.getBytes());

        return db.thenCompose(getKey).thenCompose(byteToString).thenCompose(nextKey);
    }

    @Override
    public CompletableFuture<Map.Entry<String,String>> next(){

        CompletableFuture<String> value = currentKey.thenCompose(this::getKey);
        CompletableFuture<String> nextKey = currentKey.thenCompose(this::getNextKey);

        CompletableFuture<Map.Entry<String, String>> result = currentKey
                .thenCombine(value, AbstractMap.SimpleEntry::new);
        currentKey = nextKey;
        return result;
    }

    @Override
    public CompletableFuture<Boolean> hasNext(){
        Function<? super String, ? extends CompletionStage<Boolean>> isNotNull = k ->
                CompletableFuture.completedFuture(!k.equals(""));
        return currentKey.thenCompose(isNotNull);
    }
}
