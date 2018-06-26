package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapStringToListOfIntegersFactory implements FutureSecureMapFactory<String, List<Integer>> {

    private final FutureSecureMapFactory<String, List<Integer>> fsmf;

    @Inject
    public MapStringToListOfIntegersFactory(StringDataBaseFactory sdbf) {
        fsmf = new FutureSecureMapFactoryImpl<String, List<Integer>>(sdbf, i -> i,
                MapStringToListOfIntegersFactory::listOfIntegersToString, MapStringToListOfIntegersFactory::stringToListOfIntegers);
    }

    @Override
    public FutureSecureMap<String, List<Integer>> create(String name) {
        return fsmf.create(name);
    }

    static List<Integer> stringToListOfIntegers(String str) {
        return Arrays.stream((str.split("\\$"))).map(Integer::new).collect(Collectors.toList());
    }

    static String listOfIntegersToString(List<Integer> list) {
        return String.join("$", list.stream().map(String::valueOf).collect(Collectors.toList()));
    }
}
