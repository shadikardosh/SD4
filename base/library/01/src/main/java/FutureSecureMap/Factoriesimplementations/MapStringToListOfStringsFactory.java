package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class MapStringToListOfStringsFactory implements FutureSecureMapFactory<String, List<String>> {

    private final FutureSecureMapFactory<String, List<String>> fsmf;

    @Inject
    public MapStringToListOfStringsFactory(StringDataBaseFactory sdbf) {
        fsmf = new FutureSecureMapFactoryImpl<String, List<String>>(sdbf, i -> i,
                MapStringToListOfStringsFactory::listOfStringsToString,
                MapStringToListOfStringsFactory::stringToListOfStrings);
    }

    @Override
    public FutureSecureMap<String, List<String>> create(String name) {
        return fsmf.create(name);
    }

    static List<String> stringToListOfStrings(String str) {
        return Arrays.asList((str.split("\\$")));
    }

    static String listOfStringsToString(List<String> list) {
        return String.join("$", list);
    }

}
