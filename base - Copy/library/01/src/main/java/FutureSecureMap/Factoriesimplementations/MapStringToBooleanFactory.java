package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;

public class MapStringToBooleanFactory implements FutureSecureMapFactory<String, Boolean> {

    private final FutureSecureMapFactory<String, Boolean> fsmf;

    @Inject
    public MapStringToBooleanFactory(StringDataBaseFactory sdbf) {
        this.fsmf = new FutureSecureMapFactoryImpl<String, Boolean>(sdbf, i -> i, String::valueOf, Boolean::parseBoolean);
    }

    @Override
    public FutureSecureMap<String, Boolean> create(String name) {
        return fsmf.create(name);
    }
}
