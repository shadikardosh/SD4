package il.ac.technion.cs.sd.poke.app.Pokemon;

import java.lang.Math.*;
import javax.print.DocFlavor;
import java.util.*;
import java.util.stream.Collectors;

public class Pokemon {
    private String pId;
    private Map<String, Integer> bestInstanceLevelByTrainer;
    private Map<String, List<String>> trainersHistory;
    private Map<String, Integer> allHolders;

    Pokemon(String pId, String tId, Integer level){
        this.pId=pId;
        this.trainersHistory = new HashMap<>();
        List<String> hist = new ArrayList<>();
        hist.add("1");
        this.trainersHistory.put(tId.toString(), hist);
        allHolders.put(tId, 1);
        this.bestInstanceLevelByTrainer = new HashMap<>();
        bestInstanceLevelByTrainer.put(tId, level);
    }

    Pokemon(String pId){
        this.pId = pId;
        this.bestInstanceLevelByTrainer = new HashMap<>();
        this.trainersHistory = new HashMap<>();
    }

    public void catchPokemon(String tId, Integer level){
        if (trainersHistory.containsKey(tId)){
            List<String> history = trainersHistory.get(tId);
            Integer last = Integer.parseInt(history.get(history.size()-1));
            last += 1;
            history.add(last.toString());
            trainersHistory.put(tId, history);
            if(bestInstanceLevelByTrainer.get(tId) < level)
                bestInstanceLevelByTrainer.put(tId, level);
        }else{
            List<String> history = new ArrayList<>();
            history.add("1");
            trainersHistory.put(tId, history);
            bestInstanceLevelByTrainer.put(tId, level);
        }
        if(!allHolders.containsKey(tId))
            allHolders.put(tId,1);
        else
            allHolders.put(tId, allHolders.get(tId)+1);
    }

    public void releasePokemon(String tId){
        if(!trainersHistory.containsKey(tId)) return;
        List<String> history = trainersHistory.get(tId);
        Integer last = Integer.parseInt(history.get(history.size()-1));
        last -= 1;
        if(last < 0) return;
        history.add(last.toString());
        trainersHistory.put(tId, history);
        if(allHolders.containsKey(tId)) {
            allHolders.put(tId, Math.max(allHolders.get(tId)-1, 0));
            bestInstanceLevelByTrainer.remove(tId);
        }

    }

    public String getpId() {
        return pId;
    }

    public Integer getBestInstanceLevel() {
        Integer maxLevel = -1;
        for(Map.Entry<String, Integer> e: bestInstanceLevelByTrainer.entrySet()){
            if(e.getValue()>maxLevel)
                maxLevel = e.getValue();
        }
        return maxLevel;
    }

    public Map<String, List<String>> getTrainersHistory() {
        return trainersHistory;
    }

    public List<String> getCurrentHolders(){
        return allHolders.keySet().stream().filter(x->allHolders.get(x)>0).collect(Collectors.toList());
    }

    public List<String> getAllHolders(){
        return new ArrayList<String>(allHolders.keySet());
    }
}
