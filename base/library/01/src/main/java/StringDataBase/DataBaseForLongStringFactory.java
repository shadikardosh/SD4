package StringDataBase;

import DataBase.DataBaseFactory;

import javax.inject.Inject;

/**
 * A factory used for getting DataBaseForLongString instance.
 */
public class DataBaseForLongStringFactory implements StringDataBaseFactory {
    final private DataBaseFactory dbf;

    @Inject
    public DataBaseForLongStringFactory(DataBaseFactory dbf) {
        this.dbf = dbf;
    }

    @Override
    public StringDataBase create(String name) {
        return new DataBaseForLongString(dbf.create(name));
    }
}
