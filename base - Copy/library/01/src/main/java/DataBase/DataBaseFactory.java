package DataBase;


/**
 * A factory used for getting a DataBase instance.
 */
public interface DataBaseFactory {
    /**
     * Returns a DataBase with the given name.
     * If was called before with the same name, returns the old data base.
     * If hadn't, will create a new one and returns it.
     *
     * @param name the name for the DataBase
     * @return the relevant DataBase
     */
    DataBase create(String name);
}
