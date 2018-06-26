package il.ac.technion.cs.sd.poke.app.Trainer;

import java.util.*;
import java.util.stream.Collectors;

public class Trainer {
    static public List<String> validPokemons;
    private String tId;
    private Map<String, List<Integer>> pokemonLevels;
    private Map<String, List<String>> pokemonsHistory;

    Trainer(String tId, String pId, Integer level){
        this.tId = tId;
        pokemonLevels = new HashMap<>();
        List<Integer> levels =  new ArrayList<>();
        levels.add(level);
        pokemonsHistory = new HashMap<>();
        List<String> history = new ArrayList<>();
        history.add("1");
        pokemonsHistory.put(pId, history);
        pokemonLevels.put(pId, levels);
    }

    public void catchPokemon(String pId, Integer level){
        if (pokemonsHistory.containsKey(pId)){
            List<String> history = pokemonsHistory.get(pId);
            Integer last = Integer.parseInt(history.get(history.size()-1));
            last += 1;
            history.add(last.toString());
            pokemonsHistory.put(pId, history);
            pokemonLevels.get(pId).add(level);
        }else {
            List<String> history = new ArrayList<>();
            List<Integer> levels = new ArrayList<>();
            levels.add(level);
            history.add("1");
            pokemonsHistory.put(pId, history);
            pokemonLevels.put(pId, levels);
        }
    }

    public void releasePokemon(String pId){
        if(!pokemonsHistory.containsKey(pId)) return;
        List<String> history = pokemonsHistory.get(pId);
        Integer last = Integer.parseInt(history.get(history.size()-1));
        last -= 1;
        if(last < 0) return;
        history.add(last.toString());
        pokemonsHistory.put(tId, history);
        Integer minLevel = Integer.MAX_VALUE;
        for(Integer l: pokemonLevels.get(pId))
            if(l<minLevel) minLevel = l;
        pokemonLevels.get(pId).remove(minLevel);

    }

    public Map<String, List<String>> getCleanHistory(){
        Map<String, List<String>> hist = new HashMap<>();
        pokemonsHistory.forEach((k,v)->{
            if (isPokemonValid(k))
                hist.put(k, v);
        });
        return hist;
    }

    public List<String> getAllPokemons(){
        return new ArrayList<>(pokemonLevels.keySet()).stream().filter(x->isPokemonValid(x)).collect(Collectors.toList());
    }

    public List<String> getCurrentPokemons(){
        return pokemonLevels.keySet().stream().filter(x->pokemonLevels.get(x).size()>0&&isPokemonValid(x)).collect(Collectors.toList());
    }

    public Integer getBestPokemonLevel(){
        Integer maxLevel = -1;
        for(Map.Entry<String,List<Integer>> e : pokemonLevels.entrySet()){
            if (isPokemonValid(e.getKey()) && Collections.max(e.getValue()) > maxLevel)
               maxLevel = Collections.max(e.getValue());
        }
        return maxLevel;
    }

    public static void addPokemon(String pId){
        validPokemons.add(pId);
    }

    public static boolean isPokemonValid(String pId){
        return validPokemons.contains(pId);
    }

}
