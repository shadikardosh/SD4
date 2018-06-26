import com.google.inject.Guice;
import com.google.inject.Injector;
import lib.*;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

import static org.junit.Assert.*;

public class FreeBeerDBTest {

    public class byteBufferCmp implements Comparator<ByteBuffer> {
        @Override
        public int compare(ByteBuffer b1, ByteBuffer b2) {
            try {
                String key1 = new String(b1.array(), "UTF-8");
                String key2 = new String(b2.array(), "UTF-8");
                Integer key1_payment = Integer.parseInt(key1.split("_@_")[0]);
                Integer key2_payment = Integer.parseInt(key2.split("_@_")[0]);
                if (key2_payment.compareTo(key1_payment) == 0){
                    return key1.split("_@_")[1].compareTo(key2.split("_@_")[1]);
                }
                return key1_payment.compareTo(key2_payment);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }
    private static FreeBeerDB setupDB() throws DataFormatException {
        Injector injector = Guice.createInjector(new LibraryTestModule());
        FreeBeerDB db = injector.getInstance(FreeBeerDB.class);

        /* It's important to call join() on all addEntry before querying DB. Otherwise values might still not be added */
        db.addEntry("Tom Cruise".getBytes(), ByteBuffer.allocate(4).putInt(55).array()).join();
        db.addEntry("Leonardo DiCaprio".getBytes(), ByteBuffer.allocate(4).putInt(43).array()).join();
        db.addEntry("Julia Roberts".getBytes(), ByteBuffer.allocate(4).putInt(50).array()).join();
        db.addEntry("Jackie Chan".getBytes(), ByteBuffer.allocate(4).putInt(62).array()).join();

        return db;
    }

    private static FreeBeerDB setupDBToSort() throws DataFormatException {
        Injector injector = Guice.createInjector(new LibraryTestModule());
        FreeBeerDB db = injector.getInstance(FreeBeerDB.class);

        /* It's important to call join() on all addEntry before querying DB. Otherwise values might still not be added */
        db.addEntry("123_@_Tom Cruise".getBytes(), "543_@_Mission Impossible".getBytes()).join();
        db.addEntry("11_@_Leonardo DiCaprio".getBytes(), "432_@_Catch Me If You Can".getBytes()).join();
        db.addEntry("9_@_Julia Roberts".getBytes(), "1_@_Pretty Woman".getBytes()).join();
        db.addEntry("1000_@_Jackie Chan".getBytes(), "432_@_Shanghai Knights".getBytes()).join();
        db.addEntry("1000_@_Angelina Jolie".getBytes(), "432_@_Mr & Mrs Smith".getBytes()).join();
        db.addEntry("9_@_Hugh Jackman".getBytes(), "1_@_Wolverine".getBytes()).join();

        return db;
    }

    @Test
    public void testAdd() {
        try {
            FreeBeerDB db = setupDB();

            assertEquals(ByteBuffer.wrap(db.get("Tom Cruise".getBytes()).get()).getInt(), 55);
            assertEquals(ByteBuffer.wrap(db.get("Jackie Chan".getBytes()).get()).getInt(), 62);
            assertEquals(ByteBuffer.wrap(db.get("Leonardo DiCaprio".getBytes()).get()).getInt(), 43);
            assertEquals(ByteBuffer.wrap(db.get("Julia Roberts".getBytes()).get()).getInt(), 50);

        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testGet(){
        Injector injector = Guice.createInjector(new LibraryTestModule());
        FreeBeerDB db = injector.getInstance(FreeBeerDB.class);
        try {
            db.get("Tom Cruise".getBytes());
            fail();
        }catch (NoSuchElementException e) {
        }

        try {
            db.addEntry("Jackie Chan".getBytes(), ByteBuffer.allocate(Integer.SIZE).putInt(64).array()).join();
            assertEquals(ByteBuffer.wrap(db.get("Jackie Chan".getBytes()).get()).getInt(), 64);
            db.addEntry("Jackie Chan".getBytes(), ByteBuffer.allocate(Integer.SIZE).putInt(62).array()).join();
            assertEquals(ByteBuffer.wrap(db.get("Jackie Chan".getBytes()).get()).getInt(), 62);
            db.addEntry("Jackie chan".getBytes(), ByteBuffer.allocate(Integer.SIZE).putInt(66).array()).join();
            assertEquals(ByteBuffer.wrap(db.get("Jackie Chan".getBytes()).get()).getInt(), 62);
            assertEquals(ByteBuffer.wrap(db.get("Jackie chan".getBytes()).get()).getInt(), 66);
        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testContains() {
        try {
            FreeBeerDB db = setupDB();

            assertTrue(db.contains("Tom Cruise".getBytes()).get());
            assertTrue(db.contains("Jackie Chan".getBytes()).get());
            assertTrue(db.contains("Leonardo DiCaprio".getBytes()).get());
            assertTrue(db.contains("Julia Roberts".getBytes()).get());
            assertFalse(db.contains("Julia roberts".getBytes()).get());
            assertFalse(db.contains("Leonardo Dicaprio".getBytes()).get());
        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testKeys(){
        try {
            FreeBeerDB db = setupDB();

            List<ByteBuffer> dbKeys = new ArrayList<>();
            dbKeys.add(ByteBuffer.wrap("Tom Cruise".getBytes()));
            dbKeys.add(ByteBuffer.wrap("Leonardo DiCaprio".getBytes()));
            dbKeys.add(ByteBuffer.wrap("Julia Roberts".getBytes()));
            dbKeys.add(ByteBuffer.wrap("Jackie Chan".getBytes()));

            assertEquals(dbKeys, db.keys().get());

        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testValues(){

        try {
            FreeBeerDB db = setupDB();
            db.addEntry("Amy Adams".getBytes(), ByteBuffer.allocate(4).putInt(43).array()).join();

            List<Integer> dbValues = new ArrayList<>();

            dbValues.add(55);
            dbValues.add(43);
            dbValues.add(50);
            dbValues.add(62);
            dbValues.add(43);

            List<Integer> resolvedValues = new ArrayList<>();
            for (ByteBuffer value : db.values().get()) {
                resolvedValues.add(value.getInt());
            }
            assertEquals(dbValues, resolvedValues);

        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEntries(){

        try {
            FreeBeerDB db = setupDB();

            Map<String, Integer> expectedEntries = new TreeMap<>();
            expectedEntries.put("Tom Cruise", 55);
            expectedEntries.put("Leonardo DiCaprio", 43);
            expectedEntries.put("Julia Roberts", 50);
            expectedEntries.put("Jackie Chan", 62);

            Map<String, Integer> resolvedValues = new TreeMap<>();
            for (Map.Entry<ByteBuffer, ByteBuffer> entry : db.entries().get().entrySet()) {
                resolvedValues.put(new String(entry.getKey().array(), "UTF-8"), entry.getValue().getInt());
            }
            assertEquals(expectedEntries, resolvedValues);

        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSortedKeys(){
        try {
            FreeBeerDB db = setupDBToSort();

            List<String> dbKeys = new ArrayList<>();
            dbKeys.add("9_@_Hugh Jackman");
            dbKeys.add("9_@_Julia Roberts");
            dbKeys.add("11_@_Leonardo DiCaprio");
            dbKeys.add("123_@_Tom Cruise");
            dbKeys.add("1000_@_Angelina Jolie");
            dbKeys.add("1000_@_Jackie Chan");

            List<String> resolvedValues = new ArrayList<>();
            for (ByteBuffer key : db.sortedKeys(new byteBufferCmp()).get()) {
                resolvedValues.add(new String(key.array(), "UTF-8"));
            }
            assertEquals(dbKeys, resolvedValues);

        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSortedValues(){
        try {
            FreeBeerDB db = setupDBToSort();

            List<String> dbValues = new ArrayList<>();
            dbValues.add("1_@_Pretty Woman");
            dbValues.add("1_@_Wolverine");
            dbValues.add("432_@_Catch Me If You Can");
            dbValues.add("432_@_Mr & Mrs Smith");
            dbValues.add("432_@_Shanghai Knights");
            dbValues.add("543_@_Mission Impossible");

            List<String> resolvedValues = new ArrayList<>();
            for (ByteBuffer value : db.sortedValues(new byteBufferCmp()).get()) {
                resolvedValues.add(new String(value.array(), "UTF-8"));
            }
            assertEquals(dbValues, resolvedValues);

        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMaxValue(){
        try {
            FreeBeerDB db = setupDBToSort();
            // in '.get().get()' 1st get() is of CompletableFuture, second one is of Optional
            assertEquals("543_@_Mission Impossible", new String (db.maxValue(new byteBufferCmp()).get().get().array(), "UTF-8"));

        } catch (DataFormatException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            fail();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSerializedLargeValue() throws DataFormatException, ExecutionException, InterruptedException {
        FreeBeerDB db = setupDB();

        int n_entries = 1000;
        {
            TreeMap<String, Integer> map = new TreeMap<>();

            for (int i = 0; i < n_entries; i++) {
                Integer random = 1024 * 1024 + new Random().nextInt(1024 * 1024 * 1024);
                map.put(random.toString(), random);
            }

            byte[] data = SerializationUtils.serialize(map);

            try {
                db.addEntry("map".getBytes(), data).join();
                assertEquals(map, SerializationUtils.deserialize(db.get("map".getBytes()).get()));
            } catch (DataFormatException e) {
                e.printStackTrace();
                fail();
            }
        }
        {
            ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i < n_entries; i++) {
                Integer random = 1024 * 1024 + new Random().nextInt(1024 * 1024 * 1024);
                list.add(random.toString());
            }
            byte[] data = SerializationUtils.serialize(list);

            try {
                db.addEntry("list".getBytes(), data).join();
                assertEquals(list, SerializationUtils.deserialize(db.get("list".getBytes()).get()));
            } catch (DataFormatException e) {
                e.printStackTrace();
                fail();
            }
        }
    }

}
