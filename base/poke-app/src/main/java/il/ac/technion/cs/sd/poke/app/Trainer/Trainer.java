package il.ac.technion.cs.sd.poke.app.Trainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trainer {
    static public List<String> validPokemons;
    private String tId;
    private Map<String, Integer> bestPokemonLevel;
    private Map<String, List<String>> pokemonsHistory;

    Trainer(String tId, String pId, Integer level){
        this.tId = tId;
        bestPokemonLevel = new HashMap<>();
        pokemonsHistory = new HashMap<>();
        bestPokemonLevel.put(pId, level);
        List<String> history = new ArrayList<>();
        history.add("1");
        pokemonsHistory.put(pId, history);
    }

    public void catchPokemon(String pId, Integer level){
        if (pokemonsHistory.containsKey(pId)){
            List<String> history = pokemonsHistory.get(pId);
            Integer last = Integer.parseInt(history.get(history.size()-1));
            last += 1;
            history.add(last.toString());
            pokemonsHistory.put(pId, history);
            if (bestPokemonLevel.get(pId) < level)
                bestPokemonLevel.put(pId, level);
        }else {
            List<String> history = new ArrayList<>();
            history.add("1");
            pokemonsHistory.put(pId, history);
            bestPokemonLevel.put(pId, level);
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
        bestPokemonLevel.remove(pId);
    }

    public Map<String, List<String>> getCleanHistory(){
        Map<String, List<String>> hist = new HashMap<>();
        pokemonsHistory.forEach((k,v)->{
            if (isPokemonValid(k))
                hist.put(k, v);
        });
        return hist;
    }

    public Integer getBestPokemonLevel(){
        Integer maxLevel = -1;
        for(Map.Entry<String,Integer> e : bestPokemonLevel.entrySet()){
            if (isPokemonValid(e.getKey()) && e.getValue() > maxLevel)
               maxLevel = e.getValue();
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
