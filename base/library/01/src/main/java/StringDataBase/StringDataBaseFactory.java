package StringDataBase;

/**
 * A factory used for getting a StringDataBase instance.
 */
public interface StringDataBaseFactory {
    /**
     * Returns a StringDataBase with the given name.
     * If was called before with the same name, returns the old StringDataBase.
     * If hadn't, will create a new one and returns it.
     *
     * @param name the name for the StringDataBase.
     * @return the relevant StringDataBase.
     */
    StringDataBase create(String name);
}
