package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MapStringToMapFromStringToListOfLongsFactory implements FutureSecureMapFactory<String, Map<String, List<Long>>> {

    private final FutureSecureMapFactory<String, Map<String, List<Long>>> fsmf;

    @Inject
    public MapStringToMapFromStringToListOfLongsFactory(StringDataBaseFactory sdbf) {
        this.fsmf = new FutureSecureMapFactoryImpl<String, Map<String, List<Long>>>(sdbf, i -> i,
                MapStringToMapFromStringToListOfLongsFactory::stringFromMapFromStringToListOfString,
                MapStringToMapFromStringToListOfLongsFactory::stringToMopFromStringToListOfString);
    }

    @Override
    public FutureSecureMap<String, Map<String, List<Long>>> create(String name) {
        return fsmf.create(name);
    }

    static String stringFromMapFromStringToListOfString(Map<String, List<Long>> value) {
        StringBuilder element = new StringBuilder();
        for (Map.Entry<String, List<Long>> entry : value.entrySet()) {
            element.append(entry.getKey()).append("&").append(String.join("#", entry.getValue().stream().map(String::valueOf).collect(Collectors.toList()))).append("$");
        }
        return element.toString();
    }

    static Map<String, List<Long>> stringToMopFromStringToListOfString(String str) {
        String[] map_entries = str.split("\\$");
        Map<String, List<Long>> map = new TreeMap<>();
        for (String entry_str : map_entries) {
            String[] key_and_value = entry_str.split("&");
            map.put(key_and_value[0], Arrays.stream(key_and_value[1].split("#")).map(Long::new).collect(Collectors.toList()));
        }
        return map;
    }
}
