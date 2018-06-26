package FutureSecureMap.Factoriesimplementations;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MapStringToListOfStringsFactoryTest {

    @Test
    public void valueToStringToValueEqualsToValue() {
        List<String> list = Arrays.asList("sadasd", "sadjasd", "asdjsakd", "sadjaskdsadhjjkvc", "asdbksd adsahds askhd sadm da as   dk", "sad", "132");
        assertEquals(list, MapStringToListOfStringsFactory.stringToListOfStrings(MapStringToListOfStringsFactory.listOfStringsToString(list)));
    }
}