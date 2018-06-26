package DataBase;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.zip.DataFormatException;


/**
 * A database which enabling storage of big values.
 */
public class DataBaseForBigValues implements DataBase {

    final private FutureSecureDatabaseFactory factory;
    private CompletableFuture<FutureSecureDatabase> future_data_base;

    private String name;

    private final static Integer max_value_size = 100;
    private final static Integer int_size = 4;

    @Inject
    DataBaseForBigValues(FutureSecureDatabaseFactory pf, String name) {
        this.factory = pf;
        this.name = name;
        future_data_base = pf.open(name);
    }

    public CompletableFuture<Void> addEntry(byte[] key, byte[] value) {

        return future_data_base.thenCompose(data_base -> {

            CompletableFuture<Void> ret = CompletableFuture.completedFuture(null);
            for (int i = 0; i < value.length; i += max_value_size) {

                byte[] extension = ByteBuffer.allocate(int_size).putInt(i / max_value_size).array();
                byte[] key_with_extension = new byte[key.length + extension.length];
                System.arraycopy(key, 0, key_with_extension, 0, key.length);
                System.arraycopy(extension, 0, key_with_extension, key.length, extension.length);

                Integer current_value_length = Integer.min(max_value_size, value.length - i);
                byte[] current_value = new byte[current_value_length];
                System.arraycopy(value, i, current_value, 0, current_value_length);
                try {
                    ret = CompletableFuture.allOf(ret, data_base.addEntry(key_with_extension, current_value));
                } catch (DataFormatException e) {
                    System.out.println("DataBaseForBigValues:data array is too long BUG !!!!!!!!");
                    throw new RuntimeException();
                }
            }
            return ret;
        });

    }

    public CompletableFuture<byte[]> get(byte[] key) {
        return future_data_base.thenCompose(data_base -> {
            CompletableFuture<byte[]> result = CompletableFuture.completedFuture(new byte[0]);

            for (int i = 0; ; i += max_value_size) {
                byte[] extension = ByteBuffer.allocate(int_size).putInt(i / max_value_size).array();
                byte[] key_with_extension = new byte[key.length + extension.length];
                System.arraycopy(key, 0, key_with_extension, 0, key.length);
                System.arraycopy(extension, 0, key_with_extension, key.length, extension.length);

                try {
                    CompletableFuture<byte[]> current_array = data_base.get(key_with_extension);
                    result = result.thenCombine(current_array, this::combineBytesArrays);
                } catch (NoSuchElementException e) {
                    if (i == 0) {
                        throw new NoSuchElementException();
                    }
                    break;
                }
            }

            return result;

        });
    }

    private byte[] combineBytesArrays(byte[] arr1, byte[] arr2) {
        byte[] result = new byte[arr1.length + arr2.length];

        for (int i = 0; i < arr1.length; i++) {
            result[i] = arr1[i];
        }

        for (int i = 0; i < arr2.length; i++) {
            result[arr1.length + i] = arr2[i];
        }
        return result;
    }
}
