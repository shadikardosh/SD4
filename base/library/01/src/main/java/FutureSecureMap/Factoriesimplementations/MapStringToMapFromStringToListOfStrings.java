package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapStringToMapFromStringToListOfStrings implements FutureSecureMapFactory<String, Map<String, List<String>>> {

    private final FutureSecureMapFactory<String, Map<String, List<String>>> fsmf;

    @Inject
    public MapStringToMapFromStringToListOfStrings(StringDataBaseFactory sdbf) {
        this.fsmf = new FutureSecureMapFactoryImpl<String, Map<String, List<String>>>(sdbf, i -> i,
                MapStringToMapFromStringToListOfStrings::stringFromMapFromStringToListOfString,
                MapStringToMapFromStringToListOfStrings::stringToMopFromStringToListOfString);
    }

    @Override
    public FutureSecureMap<String, Map<String, List<String>>> create(String name) {
        return fsmf.create(name);
    }

    static String stringFromMapFromStringToListOfString(Map<String, List<String>> value) {
        StringBuilder element = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : value.entrySet()) {
            element.append(entry.getKey()).append("&").append(String.join("#", entry.getValue())).append("$");
        }
        return element.toString();
    }

    static Map<String, List<String>> stringToMopFromStringToListOfString(String str) {
        String[] map_entries = str.split("\\$");
        Map<String, List<String>> map = new TreeMap<>();
        for (String entry_str : map_entries) {
            String[] key_and_value = entry_str.split("&");
            map.put(key_and_value[0], Arrays.asList(key_and_value[1].split("#")));
        }
        return map;
    }
}
