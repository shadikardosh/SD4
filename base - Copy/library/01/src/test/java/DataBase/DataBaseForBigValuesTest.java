package DataBase;

import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabase;
import il.ac.technion.cs.sd.movie.ext.FutureSecureDatabaseFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertArrayEquals;

public class DataBaseForBigValuesTest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(30);

    @Test
    public void testGetWithLessThan100BytesResult() throws InterruptedException, ExecutionException {

        FutureSecureDatabase db_test = Mockito.mock(FutureSecureDatabase.class);
        FutureSecureDatabaseFactory factory_test = Mockito.mock(FutureSecureDatabaseFactory.class);

        byte[] key_bytes = "1".getBytes();
        byte[] extension = ByteBuffer.allocate(4).putInt(0).array();
        byte[] key_with_extension = new byte[key_bytes.length + extension.length];
        System.arraycopy(key_bytes, 0, key_with_extension, 0, key_bytes.length);
        System.arraycopy(extension, 0, key_with_extension, key_bytes.length, extension.length);

        byte[] key_bytes1 = "1".getBytes();
        byte[] extension1 = ByteBuffer.allocate(4).putInt(1).array();
        byte[] key_with_extension1 = new byte[key_bytes1.length + extension1.length];
        System.arraycopy(key_bytes1, 0, key_with_extension1, 0, key_bytes1.length);
        System.arraycopy(extension1, 0, key_with_extension1, key_bytes1.length, extension1.length);

        byte[] res_byte = "less than 100 bytes".getBytes();

        Mockito.when((db_test.get(key_with_extension))).thenReturn(CompletableFuture.completedFuture(res_byte));
        Mockito.when((db_test.get(key_with_extension1))).thenThrow(new NoSuchElementException());

        Mockito.when(factory_test.open("test1")).thenReturn(CompletableFuture.completedFuture(db_test));


        DataBaseForBigValues db = new DataBaseForBigValues(factory_test, "test1");

//        System.out.println(new String(db.get("1".getBytes())));
//        System.out.println(new String(res_byte));

        assertThat(db.get("1".getBytes()).get(), equalTo(res_byte));
        assertArrayEquals(db.get("1".getBytes()).get(), res_byte);
    }

    @Test
    public void testGetWithMoreThan100BytesResult() throws InterruptedException, ExecutionException {

        FutureSecureDatabase db_test = Mockito.mock(FutureSecureDatabase.class);
        FutureSecureDatabaseFactory factory_test = Mockito.mock(FutureSecureDatabaseFactory.class);

        String res0 = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        String res1 = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
        String res2 = "2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222";
        String res3 = "3333333333333333333333333333333333";

        byte[] res0_byte = (res0).getBytes();
        byte[] res1_byte = (res1).getBytes();
        byte[] res2_byte = (res2).getBytes();
        byte[] res3_byte = (res3).getBytes();

        byte[][] res_byte = {res0_byte, res1_byte, res2_byte, res3_byte};

        byte[] key_bytes = "1".getBytes();
        byte[] extension = ByteBuffer.allocate(4).putInt(0).array();
        byte[][] key_with_extension = new byte[4][key_bytes.length + extension.length];


        for(int i = 0; i < 4; i++) {

            extension = ByteBuffer.allocate(4).putInt(i).array();
            System.arraycopy(key_bytes, 0, key_with_extension[i], 0, key_bytes.length);
            System.arraycopy(extension, 0, key_with_extension[i], key_bytes.length, extension.length);
            if(i == 3) {
                Mockito.when((db_test.get(key_with_extension[i]))).thenThrow(new NoSuchElementException());
                break;
            }
            Mockito.when((db_test.get(key_with_extension[i]))).thenReturn(CompletableFuture.completedFuture(res_byte[i]));
        }


        Mockito.when(factory_test.open("test1")).thenReturn(CompletableFuture.completedFuture(db_test));


        DataBaseForBigValues db = new DataBaseForBigValues(factory_test, "test1");

//        System.out.println(new String(db.get("1".getBytes())));
//        System.out.println(new String(res_byte));

        assertArrayEquals(db.get("1".getBytes()).get(), (res0+res1+res2).getBytes());

        Mockito.when((db_test.get(key_with_extension[2]))).thenReturn(CompletableFuture.completedFuture(res_byte[3]));
        Mockito.when(factory_test.open("test1")).thenReturn(CompletableFuture.completedFuture(db_test));
        db = new DataBaseForBigValues(factory_test, "test1");

        assertArrayEquals(db.get("1".getBytes()).get(), (res0+res1+res3).getBytes());

    }
}
