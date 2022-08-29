package com.catboyranch.roborancher.managers;

import com.catboyranch.roborancher.utils.KeyValueStorage;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RuleManager {
    @Getter
    private final ArrayList<String> rules = new ArrayList<>();

    public void load(JSONObject json) {
        if(!json.has("rules"))
            return;

        JSONArray rulesJSON = json.getJSONArray("rules");

        //Load
        int maxRules = 0;
        ArrayList<KeyValueStorage<Integer, String>> loadedRules = new ArrayList<>();
        for(int i = 0; i < rulesJSON.length(); i++) {
            JSONObject rule = rulesJSON.getJSONObject(i);
            int index = rule.getInt("index");
            String text = rule.getString("text");
            if(maxRules < index)
                maxRules = index;
            loadedRules.add(new KeyValueStorage<>(index, text));
        }
        for(KeyValueStorage<Integer, String> r : loadedRules) {
            rules.add(r.getKey(), r.getValue());
        }
    }

    public boolean addRule(int index, String text) {
        try {
            rules.add(index, text);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public void removeRule(int index) {
        rules.remove(index);
    }

    public JSONArray toJSON() {
        JSONArray array = new JSONArray();
        int index = 0;
        for(String rule : rules) {
            JSONObject r = new JSONObject();
            r.put("index", index);
            r.put("text", rule);
            array.put(r);
            index++;
        }
        return array;
    }

}
