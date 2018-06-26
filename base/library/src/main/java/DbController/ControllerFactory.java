package main.java.DbController;

public interface ControllerFactory {
    Controller open(String dbName);
}
