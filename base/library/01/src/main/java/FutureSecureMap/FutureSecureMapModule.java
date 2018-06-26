package FutureSecureMap;

import FutureSecureMap.Factoriesimplementations.*;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import java.util.List;
import java.util.Map;


public class FutureSecureMapModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(new TypeLiteral<FutureSecureMapFactory<String, Boolean>>() {}).to(MapStringToBooleanFactory.class);

        bind(new TypeLiteral<FutureSecureMapFactory<String, Integer>>() {}).to(MapStringToIntegerFactory.class);

        bind(new TypeLiteral<FutureSecureMapFactory<String, Double>>() {}).to(MapStringToDoubleFactory.class);

        bind(new TypeLiteral<FutureSecureMapFactory<String, String>>() {}).to(MapStringToStringFactory.class);

        bind(new TypeLiteral<FutureSecureMapFactory<String, List<String>>>() {}).to(MapStringToListOfStringsFactory.class);

        bind(new TypeLiteral<FutureSecureMapFactory<String, List<Integer>>>() {}).to(MapStringToListOfIntegersFactory.class);

        bind(new TypeLiteral<FutureSecureMapFactory<String, List<Long>>>() {}).to(MapStringToListOfLongsFactory.class);

        bind(new TypeLiteral<FutureSecureMapFactory<String, Map<String, List<String>>>>() {}).to(MapStringToMapFromStringToListOfStrings.class);

        bind(new TypeLiteral<FutureSecureMapFactory<String, Map<String, List<Long>>>>() {}).to(MapStringToMapFromStringToListOfLongsFactory.class);

    }
}
