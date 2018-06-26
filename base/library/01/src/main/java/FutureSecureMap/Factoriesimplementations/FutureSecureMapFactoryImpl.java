package FutureSecureMap.Factoriesimplementations;

import FutureSecureMap.FutureSecureMap;
import FutureSecureMap.FutureSecureMapImpl;
import FutureSecureMap.FutureSecureMapFactory;
import StringDataBase.StringDataBaseFactory;

import javax.inject.Inject;
import java.util.function.Function;

public class FutureSecureMapFactoryImpl<KeyType, ValueType> implements FutureSecureMapFactory<KeyType, ValueType> {

    private final StringDataBaseFactory sdbf;

    private final Function<KeyType, String> keyToString;
    private final Function<ValueType, String> valueToString;
    private final Function<String, ValueType> valueFromString;


    @Inject
    public FutureSecureMapFactoryImpl(StringDataBaseFactory sdbf, Function<KeyType, String> keyToString,
                                      Function<ValueType, String> valueToString, Function<String, ValueType> valueFromString) {
        this.sdbf = sdbf;
        this.keyToString = keyToString;
        this.valueToString = valueToString;
        this.valueFromString = valueFromString;
    }

    @Override
    public FutureSecureMap<KeyType, ValueType> create(String name) {
        return new FutureSecureMapImpl<KeyType, ValueType>(sdbf.create(name), keyToString, valueToString, valueFromString);
    }
}
