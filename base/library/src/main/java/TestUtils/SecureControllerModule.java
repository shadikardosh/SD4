package main.java.TestUtils;

//import DbController.ControllerFactory;
//import DbController.SecureControllerFactory;

import com.google.inject.AbstractModule;
import main.java.DbController.ControllerFactory;
import main.java.DbController.SecureControllerFactory;

public class SecureControllerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ControllerFactory.class).to(SecureControllerFactory.class).asEagerSingleton();
    }

}
