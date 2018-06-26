package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;

public class MapStringToLongFactory implements FutureSecureMapFactory<String, Long> {

    private final FutureSecureMapFactory<String, Long> fsmf;

    @Inject
    public MapStringToLongFactory(StringDataBaseFactory sdbf) {
        this.fsmf = new FutureSecureMapFactoryImpl<String, Long>(sdbf, i -> i, String::valueOf, Long::parseLong);
    }

    @Override
    public FutureSecureMap<String, Long> create(String name) {
        return fsmf.create(name);
    }
}
