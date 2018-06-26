package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;

public class MapStringToDoubleFactory implements FutureSecureMapFactory<String, Double> {

    private final FutureSecureMapFactory<String, Double> fsmf;

    @Inject
    public MapStringToDoubleFactory(StringDataBaseFactory sdbf) {
        this.fsmf = new FutureSecureMapFactoryImpl<String, Double>(sdbf, i -> i, String::valueOf, Double::parseDouble);
    }

    @Override
    public FutureSecureMap<String, Double> create(String name) {
        return fsmf.create(name);
    }
}
