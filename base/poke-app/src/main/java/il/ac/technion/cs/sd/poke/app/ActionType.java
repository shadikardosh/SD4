package main.java.il.ac.technion.cs.sd.poke.app;

public enum ActionType {
    POKEMON, CATCH, RELEASE;
    static public ActionType get(String s){
        if (s.equals("catch"))
            return CATCH;
        if (s.equals("pokemon"))
            return POKEMON;
        return RELEASE;
    }
}