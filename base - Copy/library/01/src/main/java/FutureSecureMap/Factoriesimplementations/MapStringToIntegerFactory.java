package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;

public class MapStringToIntegerFactory implements FutureSecureMapFactory<String, Integer> {

    private final FutureSecureMapFactory<String, Integer> fsmf;

    @Inject
    public MapStringToIntegerFactory(StringDataBaseFactory sdbf) {
        this.fsmf = new FutureSecureMapFactoryImpl<String, Integer>(sdbf, i -> i, String::valueOf, Integer::parseInt);
    }

    @Override
    public FutureSecureMap<String, Integer> create(String name) {
        return fsmf.create(name);
    }
}
