package Library;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface LibraryInterface {

    class LibraryException extends Exception{
    }
    class ValueTooLongException extends LibraryException{
    }

    /**
     * Using external library in order to save pairs of key and value to database
     * When adding pair with a key that's already exists in the database, replaces the old value with the new value
     * Saves the key value in desired database with given name as parameter
     * In case no database with given name exists creates one
     * If value is more than 100 bytes throws an exception
     * @param  dataBaseName a String represents the datatbase name we want to add our key value to it
     * @param  key  a String represents a key
     * @param  value a String represent the value
     * @return CompletableFuture for the adding opertaion - in our case always waits for the add entry opertaion to be completed
     */
    CompletableFuture<Void> addEntry(String dataBaseName, String key, String value) throws LibraryException;
    /**
     * Using external library to get value from the database
     * Returns String value if the key exists in desired database
     * If not throws an exception
     * If no database with given name exists throws an exception
     * @param   key  n String represents a key of the value we want to get
     * @return  a Completable Future of the value paired to the key in desired database
     */
    CompletableFuture<Optional<String>> get(String dataBaseName, String key) throws LibraryException;
}
