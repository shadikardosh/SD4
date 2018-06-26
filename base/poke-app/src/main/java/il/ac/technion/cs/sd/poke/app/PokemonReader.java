package il.ac.technion.cs.sd.poke.app;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;


/** This class will only be instantiated by Guice after one of the setup methods has been called. */
public interface PokemonReader {
  /** Returns true if the trainer currently has the the pokemon. If the trainer is not found, returns empty. */
  CompletableFuture<Optional<Boolean>> hasPokemon(String trainerId, String pokemonId);
  /**
   * Returns true if the trainer ever had, but not necessarily currently has the the pokemon.
   * If the trainer is not found, returns empty.
   */
  CompletableFuture<Optional<Boolean>> knowsPokemon(String trainerId, String pokemonId);
  /**
   * Returns true if the trainer had the pokemon, but does not anymore.
   * If the trainer is not found, returns empty.
   */
  CompletableFuture<Optional<Boolean>> hadPokemon(String trainerId, String pokemonId);

  /**
   * Returns all the pokemons the trainer <b>currently</b> has, ordered lexicographically.
   * If the trainer is not found, returns an empty list.
   */
  CompletableFuture<List<String>> getCaughtPokemons(String trainerId);
  /**
   * Returns a map from a pokemon ID the trainer's history of roster presence.
   * A history is a list of integer values, where each value contains the number of pokemon's with this pokemon ID the trainer had.
   * For example, if the input for the trainer and pokemon
   * is "catch, let-go, catch, catch, let-go, let-go, catch", the values in the list should
   * be "1, 0, 1, 2, 1, 0, 1". If the trainer is not found, returns an empty map.
   */
  CompletableFuture<Map<String, List<Integer>>> getRosterHistory(String trainerId);
  /**
   * Returns the max level of pokemon the trainer has on their <b>active</b> roster, i.e., the max level of
   * each pokemon he currently has.
   * If the trainer is not found, returns empty. If he has no pokemons, returns 0.
   */
  CompletableFuture<OptionalInt> getBestPokemonLevel(String trainerId);

  /**
   * Returns all the trainers that <b>currently</b> have this pokemon, ordered lexicographically.
   * If the pokemon is not found, returns an empty list.
   */
  CompletableFuture<List<String>> getTrainersWithPokemon(String pokemonId);
  /**
   * Returns the max level of the pokemon <b>currently</b> owned by a trainer.
   * If the pokemon is not found, returns empty. If no trainer has this pokemon, return 0.
   */
  CompletableFuture<OptionalInt> getBestInstanceLevel(String pokemonId);
  /**
   * Returns a map from a trainer ID to their history of pokemon roster presence.
   * A history is a list of integer values, where each value contains the number of pokemon's with this pokemon ID the trainer had.
   * For example, if the input for the trainer and pokemon
   * is "catch, let-go, catch, catch, let-go, let-go, catch", the values in the list should
   * be "1, 0, 1, 2, 1, 0, 1". If the trainer is not found, returns an empty map.
   */
  CompletableFuture<Map<String, List<Integer>>> getLevels(String pokemonId);
}
