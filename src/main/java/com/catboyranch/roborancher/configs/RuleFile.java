package com.catboyranch.roborancher.configs;

import com.catboyranch.roborancher.utils.KeyValueStorage;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RuleFile {
    @Getter
    private final ArrayList<String> rules = new ArrayList<>();

    public RuleFile() {}

    public RuleFile(JSONArray json) {
        //Load
        int maxRules = 0;
        ArrayList<KeyValueStorage<Integer, String>> loadedRules = new ArrayList<>();
        for(int i = 0; i < json.length(); i++) {
            JSONObject rule = json.getJSONObject(i);
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
