package DataBase;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

import javax.inject.Inject;

/**
 * A factory used for getting DataBaseForBigValues instance.
 */
public class DataBaseForBigValuesFactory implements DataBaseFactory {

    private final FutureSecureDatabaseFactory dbf;

    @Inject
    public DataBaseForBigValuesFactory(FutureSecureDatabaseFactory dbf) {
        this.dbf = dbf;
    }

    @Override
    public DataBase create(String name) {
        return new DataBaseForBigValues(dbf, name);
    }
}
