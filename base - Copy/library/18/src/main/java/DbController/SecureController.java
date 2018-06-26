package DbController;
import CsvHandler.CsvReader;
import com.google.inject.Inject;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;


public class SecureController implements Controller {

    private final FutureSecureDatabase db;
    private CompletableFuture<Void> lastInsertion;

    private String listEntry(String mainKey, Integer order) {
        return mainKey + "_" + order.toString();
    }

    private String keyEntry(String mainKey, Integer order) {
        return listEntry(mainKey, order) + "_K_E_Y";
    }

    @Inject
    SecureController(FutureSecureDatabase db) {
        this.db = db;
        lastInsertion = null;
    }

    private CompletableFuture<Void> insertToDb(String key, String value) {
        CompletableFuture<Void> ret = CompletableFuture.completedFuture(null);
        try {
            if (lastInsertion != null)
                lastInsertion.get();
            ret = db.addEntry(key.getBytes(), value.getBytes());
            lastInsertion = ret;
        } catch (DataFormatException e) {
            System.out.println("Warning: in SecureController::insertToDb");
            System.out.println("Entry " + key + "->" + value + " wasn't added. value size should be less than 100 bytes!");
            ret = CompletableFuture.completedFuture(null);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public CompletableFuture<Void> insertDataFromCsv(String csvData) {
        CsvReader reader = new CsvReader(csvData);
        String[] data;
        while ((data = reader.readLine()) != null) {
            insertToDb(data[0], data[1]);
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> insertEntry(String key, String value) {
        return insertToDb(key, value);
    }

    public CompletableFuture<Void> insertData(Map<String, String> data) {
        data.forEach(this::insertToDb);
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> dumpList(String key, List<String> data) {
        Integer size = data.size();
        insertToDb(key, size.toString());
        for (Integer i = 0; i < size; i++)
            insertToDb(listEntry(key, i), data.get(i));
        return null;
    }

    public CompletableFuture<Void> dumpMapOfLists(String key, Map<String, List<String>> data) {
        Integer numOfEntries = data.size();
        insertToDb(key, numOfEntries.toString());
        Integer i = 0;
        for (Map.Entry<String, List<String>> e : data.entrySet()) {
            insertToDb(keyEntry(key, i), e.getKey());
            dumpList(listEntry(key, i), e.getValue());
            i++;
        }
        return null;
    }

    public CompletableFuture<String> getValue(Integer key) {
        return getValue(key.toString());
    }

    public CompletableFuture<String> getValue(String key) {
        CompletableFuture<String> ret;
        try {
            ret = db.get(key.getBytes()).thenApply(String::new);
        } catch (NoSuchElementException e) {
            ret = CompletableFuture.completedFuture(null);
        }
        return ret;
    }

    public CompletableFuture<List<String>> getList(String key) {
        CompletableFuture<List<String>> result = CompletableFuture.completedFuture(new ArrayList<String>());
        String listSize;
        try {
            listSize = getValue(key).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
        if (listSize == null) return result;

        Integer size = new Integer(listSize);
        for (Integer i = 0; i < size; i++) {
            final CompletableFuture<String> ret = getValue(listEntry(key, i));
            result = result.thenApply(l -> {
                ret.thenAccept(l::add);
                return l;
            });
        }
        return result;
    }

    public CompletableFuture<Map<String, List<String>>> getMapOfLists(String key) {
        String numOfEntries;
        CompletableFuture<Map<String, List<String>>> result = CompletableFuture.
                completedFuture(new HashMap<String, List<String>>());

        try {
            numOfEntries = getValue(key).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }

        if (numOfEntries == null) return result;

        Integer size = new Integer(numOfEntries);
        for (Integer i = 0; i < size; i++) {
            final CompletableFuture<String> finalKey = getValue(keyEntry(key, i));
            final CompletableFuture<List<String>> finalList = getList(listEntry(key, i));
            result = result.thenApply(m -> {
                finalKey.thenAccept(k -> finalList.thenAccept(l -> m.put(k, l)));
                return m;
            });
        }
        return result;
    }

    public CompletableFuture<List<String>> getValuesCorrespondingTo(List<String> keys) {
        CompletableFuture<List<String>> values = CompletableFuture.
                completedFuture(new ArrayList<String>());
        System.out.println(keys);
        System.out.println(keys.size());

        for (String key : keys) {
            values = values.thenApply(l -> {
                getValue(key).thenAccept(l::add);
                return l;
            });
         }
        return values;
    }
}