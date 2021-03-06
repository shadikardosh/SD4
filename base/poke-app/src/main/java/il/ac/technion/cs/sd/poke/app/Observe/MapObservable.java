package main.java.il.ac.technion.cs.sd.poke.app.Observe;

import main.java.il.ac.technion.cs.sd.poke.app.ActionType;
import il.ac.technion.cs.sd.poke.app.Observe;
import java.util.HashSet;
import java.util.Collection;
import java.util.Map;

public class MapObservable {
    private  Collection<il.ac.technion.cs.sd.poke.app.Observe.MapObserver> observers = new HashSet<>();
    private Map<String, String> elements;
    private ActionType type;


    public Collection<MapObserver> getObservers() {
        return observers;
    }

    public Map<String, String> getElements() {
        return elements;
    }

    public void setElements(Map<String, String> map, ActionType type) {
        this.elements = map;
        this.type = type;
        onChange();
    }

    public ActionType getType() {
        return type;
    }

    public final void listen(MapObserver ob){
        observers.add(ob);
    }

    public final void listenAll(Collection<MapObserver> col){
        col.forEach(x->listen(x));
    }

    public final void unlisten(MapObserver ob){
        observers.remove(ob);
    }
    private final void onChange(){
        observers.forEach(x->x.notify(this));
    }
}