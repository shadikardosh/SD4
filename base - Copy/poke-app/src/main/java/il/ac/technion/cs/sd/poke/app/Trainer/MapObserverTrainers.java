package il.ac.technion.cs.sd.poke.app.Trainer;

import DbController.Controller;
import il.ac.technion.cs.sd.poke.app.Observe.MapObservable;
import il.ac.technion.cs.sd.poke.app.Observe.MapObserver;
import il.ac.technion.cs.sd.poke.app.Pokemon.Pokemon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapObserverTrainers implements MapObserver {

    Map<String,Trainer> trainers = new HashMap<>();

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
        trainers.forEach((tId, t) -> {
            try {
                while (!pokemonsDB.insertEntry(tId, t.getBestPokemonLevel().toString()).get());
                Map<String, List<String>> hist = t.getCleanHistory();
                List<String> allPokemons = t.getAllPokemons();
                List<String> currentPokemons = t.getCurrentPokemons();
                pokemonsDB.dumpList(tId + ";" + "AllPokemons", allPokemons);
                pokemonsDB.dumpList(tId + ";" + "CurrentPokemons", currentPokemons);
                hist.forEach((pId,list)->
                        pokemonsDB.dumpList(tId + ";" + pId, list)
                );
            }catch (InterruptedException e){
//                    throw e;
            }catch (ExecutionException e){
//                    throw e;
            }
        });
    }

    @Override
    public void notify(MapObservable mo) {
        Map<String, String> elements = mo.getElements();
        switch (mo.getType()) {
            case POKEMON:{
                String pId = elements.get("pId");
                Trainer.addPokemon(pId);
            }
            case CATCH:{
                String pId = elements.get("pId");
                String tId = elements.get("tId");
                Integer level = Integer.parseInt(elements.get("level"));
                if(trainers.containsKey(tId)){
                    trainers.get(pId).catchPokemon(pId, level);
                }else{
                    Trainer t = new Trainer(tId, pId, level);
                    trainers.put(tId, t);
                }
                break;
            }
            case RELEASE:{
                String pId = elements.get("pId");
                String tId = elements.get("tId");
                if(trainers.containsKey(pId)){
                    trainers.get(pId).releasePokemon(tId);
                }
                break;
            }

            default:{}

        }
    }
}
