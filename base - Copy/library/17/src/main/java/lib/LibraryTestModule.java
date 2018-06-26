package lib;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

public class LibraryTestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FreeBeerDBFactory.class).to(FreeBeerDBFactoryImpl.class).in(Singleton.class);
    bind(FreeBeerDB.class).to(FreeBeerDBImpl.class);
    bind(FutureSecureDatabase.class).to(FutureDatabaseFake.class);
    bind(FutureSecureDatabaseFactory.class).to(FutureDataBaseFactoryFake.class).in(Singleton.class);
  }
}
