package FutureSecureMap.Factoriesimplementations;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MapStringToListOfIntegersFactoryTest {

    @Test
    public void valueToStringToValueEqualsToValue() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1000, 2000, 3000, 98756, 654132156);
        assertEquals(list, MapStringToListOfIntegersFactory.stringToListOfIntegers(MapStringToListOfIntegersFactory.listOfIntegersToString(list)));
    }
}