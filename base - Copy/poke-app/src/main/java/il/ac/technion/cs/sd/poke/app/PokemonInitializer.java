package il.ac.technion.cs.sd.poke.app;

import java.util.concurrent.CompletableFuture;

public interface PokemonInitializer {
  /** Saves the CSV data persistently, so that it could be run using PokemonReader. */
  CompletableFuture<Void> setupCsv(String csvData);
  /** Saves the JSON data persistently, so that it could be run using PokemonReader. */
  CompletableFuture<Void> setupJson(String jsonData);
}
