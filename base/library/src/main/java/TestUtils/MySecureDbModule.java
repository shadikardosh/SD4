package main.java.TestUtils;

import com.google.inject.AbstractModule;
import il.ac.technion.cs.sd.poke.ext.FutureSecureDatabaseFactory;
public class MySecureDbModule extends AbstractModule {
        @Override
        protected void configure() {
        bind(FutureSecureDatabaseFactory.class).to(FutureSecureDbFactoryImpl.class).asEagerSingleton();
    }
}
