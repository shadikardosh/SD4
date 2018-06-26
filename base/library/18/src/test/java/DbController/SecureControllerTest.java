package DbController;

import TestUtils.MySecureDbModule;
import TestUtils.SecureControllerModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class SecureControllerTest {

    private Controller ctrl;

    private static String getDataFromFile(String fileName) throws FileNotFoundException {
        return new Scanner(new File(SecureControllerTest.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        Injector injector = Guice.createInjector(new SecureControllerModule(), new MySecureDbModule());
        ctrl = injector.getInstance(ControllerFactory.class).open("tmpDb");
    }

    @Test
    void testInsertDataMap() throws InterruptedException {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("shadi", "3101");
        hm.put("pierre", "1310");
        hm.put("shafik", "2408");
        hm.put("liza", "1605");
        hm.put("hussam", "2108");

        ctrl.insertData(hm);
        try {
            assertEquals("3101", ctrl.getValue("shadi").get());
            assertEquals("1310", ctrl.getValue("pierre").get());
            assertEquals("2408", ctrl.getValue("shafik").get());
            assertEquals("1605", ctrl.getValue("liza").get());
            assertEquals("2108", ctrl.getValue("hussam").get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    void cantInsertEntryWithValueMoreThan100B() throws ExecutionException, InterruptedException {
        ctrl.insertEntry("ere", "dfhsafjsadhfjglhjasdfgkhjsdFHfasdflaiUGFhgjhglghljAllgaisufhasGCLuwqiydfhioqwdugwqihfiugflwqFGIlqwufguqwg");
        assertEquals(null, ctrl.getValue("ere").get());
    }

    @Test
    void getValueFromDbSimple() throws InterruptedException, ExecutionException {
        ctrl.insertDataFromCsv("1,aaa");
        assertEquals("aaa", ctrl.getValue(1).get());
    }

    @Test
    void getValueInteger() throws InterruptedException, ExecutionException {
        String csvData = "1,aaa\n2,bbb\n3,ccc\n4,ddd";
        ctrl.insertDataFromCsv(csvData);
        assertEquals("aaa", ctrl.getValue(1).get());
        assertEquals("bbb", ctrl.getValue(2).get());
        assertEquals("ccc", ctrl.getValue(3).get());
        assertEquals("ddd", ctrl.getValue(4).get());
    }

    @Test
    void getValueString() throws InterruptedException, ExecutionException {
        String csvData = "1,aaa\n2,bbb\n3,ccc\n4,ddd";
        ctrl.insertDataFromCsv(csvData);
        assertEquals("aaa", ctrl.getValue("1").get());
        assertEquals("bbb", ctrl.getValue("2").get());
        assertEquals("ccc", ctrl.getValue("3").get());
        assertEquals("ddd", ctrl.getValue("4").get());
    }

    @Test
    void dumpGetList() throws InterruptedException, ExecutionException {
        ArrayList<String> shafikData = new ArrayList<>();
        shafikData.add("asf");
        shafikData.add("5w4yqg");
        shafikData.add("5946ivj");
        shafikData.add("g54iw;wtr");
        shafikData.add("32 t4359if");
        shafikData.add("g5w5wg5g");
        shafikData.add("ags5543");
        shafikData.add("rewy3y5h");
        ctrl.dumpList("shafik", shafikData);
        assertIterableEquals(shafikData, ctrl.getList("shafik").get());
        ArrayList<String> shadiData = new ArrayList<>();
        shadiData.add("asf");
        shadiData.clear();
        shadiData.add("avdsa");
        shadiData.add("df33f aFSD");
        shadiData.add("234gq");
        shadiData.add("4325yy2");
        shadiData.add("h35363g");
        ctrl.dumpList("shadi", shadiData);
        assertIterableEquals(shadiData, ctrl.getList("shadi").get());

        ArrayList<String> lizaData = new ArrayList<>();
        ctrl.dumpList("liza", lizaData);
        assertIterableEquals(lizaData, ctrl.getList("liza").get());

        assertIterableEquals(new ArrayList<>(), ctrl.getList("pierre").get());
    }

    @Test
    void dumpGetMap() throws InterruptedException, ExecutionException {
        ArrayList<String> shafikData = new ArrayList<>();
        shafikData.add("asf");
        shafikData.add("5w4yqg");
        shafikData.add("5946ivj");
        shafikData.add("g54iw;wtr");
        shafikData.add("32 t4359if");
        shafikData.add("g5w5wg5g");
        shafikData.add("ags5543");
        shafikData.add("rewy3y5h");

        ArrayList<String> shadiData = new ArrayList<>();
        shadiData.add("sdgn6");
        shadiData.add("avdsa");
        shadiData.add("df33f aFSD");
        shadiData.add("234gq");
        shadiData.add("4325yy2");
        shadiData.add("h35363g");

        ArrayList<String> lizaData = new ArrayList<>();
        lizaData.add("shfgds");
        lizaData.add("23thgrew");
        lizaData.add("264e5653");
        lizaData.add("43y43ha");

        ArrayList<String> pierreData = new ArrayList<>();

        HashMap<String, List<String>> hashMap = new HashMap<>();
        hashMap.put("shafik", shafikData);
        hashMap.put("shadi", shadiData);
        hashMap.put("liza", lizaData);
        hashMap.put("pierre", pierreData);

        ctrl.dumpMapOfLists("Fantastic 4", hashMap);

        Map<String, List<String>> result = ctrl.getMapOfLists("Fantastic 4").get();

        assertIterableEquals(shafikData, result.get("shafik"));
        assertIterableEquals(shadiData, result.get("shadi"));
        assertIterableEquals(lizaData, result.get("liza"));
        assertIterableEquals(pierreData, result.get("pierre"));


    }

    @Test
    void getValueReturnsNullIfEntryDoesntExist() throws InterruptedException, ExecutionException {
        assertEquals(null, ctrl.getValue(1).get());
    }

    @Test
    void getValuesCorrespondingTo() throws ExecutionException, InterruptedException {
        HashMap<String, String> hm = new HashMap<>();
        ctrl.insertEntry("shadi", "3101");
        ctrl.insertEntry("pierre", "1310");
        ctrl.insertEntry("shafik", "2408");
        ctrl.insertEntry("liza", "1605");
        ctrl.insertEntry("hussam", "2108");
        ctrl.insertEntry("layla", "0208");
        assertEquals(Arrays.asList("3101", "1310", "2408", "1605", "2108", "0208"),
                ctrl.getValuesCorrespondingTo(Arrays.asList("shadi", "pierre", "shafik", "liza", "hussam", "layla")).get());
    }

}