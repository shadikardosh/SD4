package FutureSecureMap.Factoriesimplementations;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class MapStringToMapFromStringToListOfLongsFactoryTest {
    @Test
    public void valueToStringToValueEqualsToValue() {
        Map<String, List<Long>> abc_map = new TreeMap<>();
        abc_map.put("abcd", Arrays.asList(1L, 2L, 3L, 4L));
        abc_map.put("efgh", Arrays.asList(8L, 7L, 6L, 5L));
        abc_map.put("ijkl", Arrays.asList(100L, 200L, 0L, 1000L));

        assertEquals(abc_map, MapStringToMapFromStringToListOfLongsFactory.stringToMopFromStringToListOfString(
                MapStringToMapFromStringToListOfLongsFactory.stringFromMapFromStringToListOfString(abc_map)));
    }

}