package il.ac.technion.cs.sd.lib.tests;

import il.ac.technion.cs.sd.lib.InnerDataSaver;
import il.ac.technion.cs.sd.lib.InnerDataSaverClass;
import il.ac.technion.cs.sd.lib.LibIterator;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.zip.DataFormatException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class basic {


    @Test
    public void testSingleStoreData() throws DataFormatException {
        FutureSecureDatabase fsd = Mockito.mock(FutureSecureDatabase.class);
        String key = "Anny";
        String value = "Firer";
        InnerDataSaver x = new InnerDataSaverClass(CompletableFuture.completedFuture(fsd), ",");
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        Mockito.when(fsd.addEntry(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(CompletableFuture.completedFuture(null));
        x.storeData(map);

        Mockito.verify(fsd).addEntry(",".getBytes(), ("," + key).getBytes());
        Mockito.verify(fsd).addEntry((key + ",0").getBytes(), value.getBytes());
        Mockito.verify(fsd).addEntry(key.getBytes(), "1,".getBytes());
    }

    @Test
    public void complexStoreTest() throws DataFormatException, InterruptedException {
        FutureSecureDatabase mock = Mockito.mock(FutureSecureDatabase.class);
        List<Map.Entry<String, String>> l = new ArrayList<>();

        InnerDataSaver x = new InnerDataSaverClass(CompletableFuture.completedFuture(mock), ",");
        //Mockito.when(mock.get(",".getBytes())).thenReturn((",").getBytes());
        Mockito.when(mock.addEntry(ArgumentMatchers.any(), ArgumentMatchers.any())).thenAnswer((y) -> {
            byte[] key = (byte[]) y.getArguments()[0];
            byte[] value = (byte[]) y.getArguments()[1];
//            System.out.println(new String(key) + " and Value " + new String (value));
            return CompletableFuture.completedFuture(null);
        });//.thenReturn(CompletableFuture.completedFuture(null));
        for (int i = 0; i < 300; i++) {
            int v = i + 2;
            String key = "" + i;
            String value = "" + v;
            l.add(new AbstractMap.SimpleEntry<>(key, value));
        }
        x.storeData(l);

//        System.out.println("=====================================");
        for (int i = 0; i < 300; i++) {
            int v = i + 2;
            String key = "" + i;
            String value = "" + v;
//            System.out.println("Excepcting key = " + key + " value = 1," );
            Mockito.verify(mock).addEntry(key.getBytes(), ("1,").getBytes());
//            System.out.println("Excepcting key = " + key  + ",0"+ " value = " + value );
            Mockito.verify(mock).addEntry((key + ",0").getBytes(), value.getBytes());

            if (i > 0) {
//                System.out.println("Excepcting key = " + "" + (i - 1) + " value = " + ("1," + (i)) );
                Mockito.verify(mock).addEntry(("" + (i - 1)).getBytes(), ("1," + (i)).getBytes());
            } else {
//                System.out.println("Excepcting key = " + "," + " value = " + ",0" );
                Mockito.verify(mock).addEntry(",".getBytes(), ",0".getBytes());
            }

        }


    }

    @Test
    public void iteratorEmptyTest() {
        FutureSecureDatabase fsd = Mockito.mock(FutureSecureDatabase.class);

        Mockito.when(fsd.get(ArgumentMatchers.any())).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException();
        }));
        InnerDataSaver ds = new InnerDataSaverClass(CompletableFuture.completedFuture(fsd), ",");
        CompletableFuture<LibIterator> it = ds.getIterator();

        Function<LibIterator, CompletionStage<Integer>> check = x -> {
            assertEquals(null, x);
            return CompletableFuture.completedFuture(1);
        };
        it.thenCompose(check);
    }

    @Test
    public void iteratorInvalidFirstKeyTest() {
        FutureSecureDatabase fsd = Mockito.mock(FutureSecureDatabase.class);

        Mockito.when(fsd.get(ArgumentMatchers.any())).thenReturn(CompletableFuture.completedFuture("1,1".getBytes()));

        Mockito.when(fsd.get("bad".getBytes())).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException();
        }));
        InnerDataSaver ds = new InnerDataSaverClass(CompletableFuture.completedFuture(fsd), ",");
        CompletableFuture<LibIterator> it = ds.getIterator("bad");

        Function<LibIterator, CompletionStage<Integer>> check = x -> {
            assertEquals(null, x);
            return CompletableFuture.completedFuture(1);
        };
        it.thenCompose(check);
    }

    @Test
    public void iteratorTest() throws ExecutionException, InterruptedException {
        FutureSecureDatabase fsd = Mockito.mock(FutureSecureDatabase.class);

        Mockito.when(fsd.get(ArgumentMatchers.any())).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException();
        }));

        Mockito.when(fsd.get("1".getBytes())).thenReturn(CompletableFuture.completedFuture("2,2".getBytes()));
        Mockito.when(fsd.get("1,0".getBytes())).thenReturn(CompletableFuture.completedFuture("a".getBytes()));
        Mockito.when(fsd.get("1,1".getBytes())).thenReturn(CompletableFuture.completedFuture("b".getBytes()));
        Mockito.when(fsd.get("2".getBytes())).thenReturn(CompletableFuture.completedFuture("1,".getBytes()));
        Mockito.when(fsd.get("2,0".getBytes())).thenReturn(CompletableFuture.completedFuture("c".getBytes()));

        InnerDataSaver ds = new InnerDataSaverClass(CompletableFuture.completedFuture(fsd), ",");
        CompletableFuture<LibIterator> it = ds.getIterator("1");

        Function<LibIterator, CompletionStage<LibIterator>> checkNotNull = x -> {
            assertNotNull(x);
            return CompletableFuture.completedFuture(x);
        };
        it.thenCompose(checkNotNull);
        Function<Boolean, CompletionStage<Boolean>> checkTrue = x -> {
            assertTrue(x);
            return CompletableFuture.completedFuture(x);
        };
        Function<Boolean, CompletionStage<Boolean>> checkFalse = x -> {
            assertTrue(!x);
            return CompletableFuture.completedFuture(x);
        };
        Function<Map.Entry<String, String>, CompletionStage<Map.Entry<String, String>>> checkFirst = x -> {
            assertEquals(x.getKey(), "1");
            assertEquals(x.getValue(), "ab");
            return CompletableFuture.completedFuture(x);
        };
        Function<Map.Entry<String, String>, CompletionStage<Map.Entry<String, String>>> checkSecond = x -> {
            assertEquals(x.getKey(), "2");
            assertEquals(x.getValue(), "c");
            return CompletableFuture.completedFuture(x);
        };
        Function<LibIterator, CompletionStage<LibIterator>> checkIteration = iter -> {
            iter.hasNext().thenCompose(checkTrue);
            iter.next().thenCompose(checkFirst);
            iter.hasNext().thenCompose(checkTrue);
            iter.next().thenCompose(checkSecond);
            iter.hasNext().thenCompose(checkFalse);
            return CompletableFuture.completedFuture(iter);
        };
        it.thenCompose(checkIteration).get();
    }
}
