package il.ac.technion.cs.sd.poke.app.ApplicationImpl;

import com.google.gson.JsonObject;
import il.ac.technion.cs.sd.poke.app.ActionType;
import il.ac.technion.cs.sd.poke.app.PokemonInitializer;
import org.json.simple.parser.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.xml.bind.Element;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PokemonInitializerImpl implements PokemonInitializer {


    @Override
    public CompletableFuture<Void> setupCsv(String csvData) {
        return null;
    }

    @Override
    public CompletableFuture<Void> setupJson(String jsonData) {
        JSONArray a =null;
        JSONParser parser = new JSONParser();
        try {
            a = (org.json.simple.JSONArray) parser.parse(jsonData);
        } catch (Exception e) {

        }
        Map<String,String> elements = new HashMap<>();
        for(Object o : a){
            JsonObject obj = (JsonObject) o;
            ActionType type = obj.get("type").toString()
        }

    }
}
