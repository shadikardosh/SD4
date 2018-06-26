package il.ac.technion.cs.sd.poke.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.poke.app.PokemonReader;
import il.ac.technion.cs.sd.poke.app.PokemonInitializer;
import il.ac.technion.cs.sd.poke.ext.SecureDatabaseModule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ExampleTest {

  @Rule public Timeout globalTimeout = Timeout.seconds(30);

  private static Injector setupAndGetInjector(String fileName) throws Exception {
      String fileContents =
        new Scanner(new File(ExampleTest.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();
    Injector injector = Guice.createInjector(new PokemonModule(), new SecureDatabaseModule());
    PokemonInitializer pi = injector.getInstance(PokemonInitializer.class);
    CompletableFuture<Void> setup =
        fileName.endsWith("csv") ? pi.setupCsv(fileContents) : pi.setupJson(fileContents);
    setup.get();
    return injector;
  }

  @Test
  public void testSimpleCsv() throws Exception {
    Injector injector = setupAndGetInjector("small.csv");
    PokemonReader reader = injector.getInstance(PokemonReader.class);
    assertEquals(Arrays.asList(1, 2, 1, 0), reader.getRosterHistory("ash").get().get("149"));
    assertEquals(90, reader.getBestPokemonLevel("misty").get().getAsInt());
    assertEquals(90, reader.getBestInstanceLevel("149").get().getAsInt());
  }

  @Test
  public void testSimpleJson() throws Exception {
    Injector injector = setupAndGetInjector("small.json");
    PokemonReader reader = injector.getInstance(PokemonReader.class);
    assertEquals(100, reader.getBestPokemonLevel("ash").get().getAsInt());
    assertFalse(reader.getBestPokemonLevel("gary").get().isPresent());
  }
}
