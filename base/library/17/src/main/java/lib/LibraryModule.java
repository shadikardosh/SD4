package lib;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

public class LibraryModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FreeBeerDBFactory.class).to(FreeBeerDBFactoryImpl.class).in(Singleton.class);
    bind(FreeBeerDB.class).to(FreeBeerDBImpl.class);
  }
}
