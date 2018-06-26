package il.ac.technion.cs.sd.poke.app.Pokemon;
import il.ac.technion.cs.sd.poke.app.ActionType;
import il.ac.technion.cs.sd.poke.app.Observe.MapObservable;
import il.ac.technion.cs.sd.poke.app.Observe.MapObserver;
import DbController.Controller;


import java.util.*;
import java.util.concurrent.ExecutionException;

public class MapObserverPokemons implements MapObserver {

    Map<String,Pokemon> pokemons = new HashMap<>();

//    @Override
//    public Map<String, String> getMap() {
//        Map<String, String> map = new HashMap<>();
//        for (Map.Entry<String, Movie> e: movies.entrySet()){
//            String mId = e.getKey();
//            Movie m = e.getValue();
//            List<String> mapVals = m.asMapValues();
//            Integer i = 0;
//            for (String v: mapVals){
//                map.put(mId+"-"+i.toString(), v);
//                i++;
//            }
//        }
//        return map;
//    }

    @Override
    public void initDB(Controller pokemonsDB) throws InterruptedException, ExecutionException{
//        try{
            pokemons.forEach((pId, p) -> {
                try {
                    while (!pokemonsDB.insertEntry(pId, p.getBestInstanceLevel().toString()).get());
                    Map<String, List<String>> hist = p.getTrainersHistory();
//                    List<String> trainers = Arrays.asList(hist.keySet());
                    hist.forEach((tId,list)-> {
                        try {
                            pokemonsDB.dumpList(pId + ";" + tId, list).get() ;
                        }catch (InterruptedException e){
//                            throw e;
                        }catch (ExecutionException e){
//                            throw e;
                        }
                    });
                }catch (InterruptedException e){
//                    throw e;
                }catch (ExecutionException e){
//                    throw e;
                }
            });
//        } catch(InterruptedException e){
//            throw e;
//        }catch (ExecutionException e){
//            throw e;
//       }
//
    }

    @Override
    public void notify(MapObservable mo) {
        Map<String, String> elements = mo.getElements();
        switch (mo.getType()) {
            case POKEMON:{
                String pId = elements.get("pId");
                if (!pokemons.containsKey(pId)){
                    Pokemon p = new Pokemon(pId);
                    pokemons.put(pId, p);
                }
                break;
            }
            case CATCH:{
                String pId = elements.get("pId");
                String tId = elements.get("tId");
                Integer level = Integer.parseInt(elements.get("level"));
                if(pokemons.containsKey(pId)){
                    pokemons.get(pId).catchPokemon(tId, level);
                }else{
                    Pokemon p = new Pokemon(pId, tId, level);
                    pokemons.put(pId, p);
                }
                break;
            }
            case RELEASE:{
                String pId = elements.get("pId");
                String tId = elements.get("tId");
                if(pokemons.containsKey(pId)){
                    pokemons.get(pId).releasePokemon(tId);
                }
                break;
            }

            default:{}

        }
    }
}
