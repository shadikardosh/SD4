package FutureSecureMap.Factoriesimplementations;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class MapStringToListOfLongsFactoryTest {

    @Test
    public void valueToStringToValueEqualsToValue() {
        List<Long> list = Arrays.asList(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L,1000L,2000L,3000L,98756L, 654132156L);
        assertEquals(list, MapStringToListOfLongsFactory.stringToListOfLongs(MapStringToListOfLongsFactory.listOfLongsToString(list)));
    }
}