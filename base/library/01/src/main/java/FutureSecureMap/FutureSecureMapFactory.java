package FutureSecureMap;

/**
 * A factory used for getting a FutureSecureMap instance.
 *
 * @param <KeyType>   the class of the key which will be used.
 * @param <ValueType> the class of the values which will be stored.
 */
public interface FutureSecureMapFactory<KeyType, ValueType> {

    /**
     * returns a StringKeyMap of a with a given name.
     * If a StringKeyMap with the given name already exists, returns it.
     * Else, create a new one, and returns it.
     *
     * @param name the name for the data base.
     * @return StringKeyMap with the given name.
     */
    FutureSecureMap<KeyType, ValueType> create(String name);
}
