package il.ac.technion.cs.sd.lib.tests;

import com.sun.deploy.util.ArrayUtil;
import il.ac.technion.cs.sd.lib.*;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;

/**
 * Example of how we used the Libiterator :)
 */
public class IteratorExample {

    @Test
    public void iterationTest() throws DatabaseAlreadyOpen, NoSuchDatabaseOpen, ExecutionException, InterruptedException {
        FutureSecureDatabaseFactory mock = setMock();
        List<Map.Entry<String,String>> expected = setExpectedResult();
        DataSaver ds = new DataSaverClass(mock);
        //first open the database
        ds.openDatabase("myData");
        //next ask for iterator
        CompletableFuture<LibIterator> it = ds.iterator("myData", "1");
        //Since we don't want to use "get" we'll use thenCompose to call a sort of recursion to add
        //values while the iterator has next.
        CompletableFuture<List<Map.Entry<String,String>>> iterationResults = it.thenCompose(this::doIteration);
        assertEquals(expected,iterationResults.get());
    }

    private CompletionStage<List<Map.Entry<String,String>>> doIteration(LibIterator iter) {
        //You should probably read this code from the last line up... it will make more sense

        //Finally we get the current value!
        Function< Map.Entry<String, String>, CompletionStage<List<Map.Entry<String, String>>>> handleNext = (current) ->
        {
            List<Map.Entry<String, String>> head = new ArrayList<>();
            head.add(current);
            //joining 2 lists: head (the current value) and tail (the result of the recursion)
            Function<List<Map.Entry<String, String>>, CompletionStage<List<Map.Entry<String, String>>>> addRest = (tail) ->
                    CompletableFuture.completedFuture(Stream.concat(head.stream(), tail.stream()).collect(Collectors.toList()));
            //We call doIteration recursively and add the current value to the start of the result
            return doIteration(iter).thenCompose(addRest);
        };
        //return empty array or handle next value (depending if there is a next)
        Function< Boolean, CompletionStage<List<Map.Entry<String, String>>>> handleHasNext = (hasNext) ->
                (hasNext) ? iter.next().thenCompose(handleNext) : CompletableFuture.completedFuture(new ArrayList<>());
        //we use hasNext() since everything is annoying completableFuture we have to use thenCompose to handle the result.
        return iter.hasNext().thenCompose(handleHasNext);
    }

    private List<Map.Entry<String, String>> setExpectedResult() {
        List<Map.Entry<String,String>> result = new ArrayList<>();
        StringBuilder a = new StringBuilder();
        for (int i = 0; i < 101; i++){
            a.append("a");
        }
        result.add(new AbstractMap.SimpleEntry<>("1", a.toString()));
        result.add(new AbstractMap.SimpleEntry<>("2", "bbb"));
        return result;
    }

    //Just using our insider knowledge of the library implementation to set a mock
    private FutureSecureDatabaseFactory setMock() {
        FutureSecureDatabaseFactory mockFactory = Mockito.mock(FutureSecureDatabaseFactory.class);
        FutureSecureDatabase mockDB = Mockito.mock(FutureSecureDatabase.class);
        Mockito.when(mockFactory.open("myData")).thenReturn(CompletableFuture.completedFuture(mockDB));
        Mockito.when(mockDB.get("1".getBytes())).thenReturn(CompletableFuture.completedFuture("2,2".getBytes()));
        StringBuilder a = new StringBuilder();
        for (int i = 0; i < 100; i++){
            a.append("a");
        }
        Mockito.when(mockDB.get("1,0".getBytes())).thenReturn(CompletableFuture.completedFuture(a.toString().getBytes()));
        Mockito.when(mockDB.get("1,1".getBytes())).thenReturn(CompletableFuture.completedFuture("a".getBytes()));
        Mockito.when(mockDB.get("2".getBytes())).thenReturn(CompletableFuture.completedFuture("1,".getBytes()));
        Mockito.when(mockDB.get("2,0".getBytes())).thenReturn(CompletableFuture.completedFuture("bbb".getBytes()));
        return mockFactory;
    }
}
