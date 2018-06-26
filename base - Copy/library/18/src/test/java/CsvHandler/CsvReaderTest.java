package CsvHandler;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

public class CsvReaderTest {

    private CsvReader reader;

    @Test public void returnFirstLineTest(){
        reader = new CsvReader("1,foo,5");
        String[] expected = {"1", "foo", "5"};
        assertArrayEquals(reader.readLine(), expected);
    }
    @Test public void identifyNewlinesTest(){
        reader = new CsvReader("a\n1,foo,5\r\nx");
        String[] expected1 = {"1", "foo", "5"};
        String[] expected2 = {"x"};
        reader.readLine();
        assertArrayEquals(reader.readLine(), expected1);
        assertArrayEquals(reader.readLine(), expected2);
    }
    @Test public void returnNullOnEmptyStringTest(){
        CsvReader emptyReader = new CsvReader("");
        assertNull(emptyReader.readLine());
    }
    @Test public void returnNullOnEndOfStringTest(){
        reader = new CsvReader("1,foo,5");
        reader.readLine();
        assertNull(reader.readLine());
    }
    @Test public void returnNullAfterStringAlreadyExhausted(){
        reader = new CsvReader("1,foo,5");
        reader.readLine();
        reader.readLine(); /* now string is exhausted */
        assertNull(reader.readLine()); /* already string is exhausted */
    }
}