package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapStringToListOfLongsFactory implements FutureSecureMapFactory<String, List<Long>> {

    private final FutureSecureMapFactory<String, List<Long>> fsmf;

    @Inject
    public MapStringToListOfLongsFactory(StringDataBaseFactory sdbf) {
        fsmf = new FutureSecureMapFactoryImpl<String, List<Long>>(sdbf, i -> i,
                MapStringToListOfLongsFactory::listOfLongsToString, MapStringToListOfLongsFactory::stringToListOfLongs);
    }

    @Override
    public FutureSecureMap<String, List<Long>> create(String name) {
        return fsmf.create(name);
    }

    static List<Long> stringToListOfLongs(String str) {
        return Arrays.stream((str.split("\\$"))).map(Long::new).collect(Collectors.toList());
    }

    static String listOfLongsToString(List<Long> list) {
        return String.join("$", list.stream().map(String::valueOf).collect(Collectors.toList()));
    }
}
