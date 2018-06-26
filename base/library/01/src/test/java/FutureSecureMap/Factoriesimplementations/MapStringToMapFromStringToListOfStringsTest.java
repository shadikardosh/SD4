package FutureSecureMap.Factoriesimplementations;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class MapStringToMapFromStringToListOfStringsTest {

    @Test
    public void valueToStringToValueEqualsToValue() {
        Map<String, List<String>> abc_map = new TreeMap<>();
        abc_map.put("abcd", Arrays.asList("a", "b", "c", "d"));
        abc_map.put("efgh", Arrays.asList("e", "f", "g", "h"));
        abc_map.put("ijkl", Arrays.asList("i", "j", "k", "l"));


        assertEquals(abc_map, MapStringToMapFromStringToListOfStrings.stringToMopFromStringToListOfString(
                MapStringToMapFromStringToListOfStrings.stringFromMapFromStringToListOfString(abc_map)));
    }

}