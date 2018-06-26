package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;

public class MapStringToStringFactory implements FutureSecureMapFactory<String, String> {

    private final FutureSecureMapFactory<String, String> fsmf;

    @Inject
    public MapStringToStringFactory(StringDataBaseFactory sdbf) {
        this.fsmf = new FutureSecureMapFactoryImpl<String, String>(sdbf, i -> i, i -> i, i -> i);
    }

    @Override
    public FutureSecureMap<String, String> create(String name) {
        return fsmf.create(name);
    }
}
