package DbController;

public interface ControllerFactory {
    Controller open(String dbName);
}
