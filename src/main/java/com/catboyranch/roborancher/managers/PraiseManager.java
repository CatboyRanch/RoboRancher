package com.catboyranch.roborancher.managers;

import com.catboyranch.roborancher.utils.TimeUtils;
import net.dv8tion.jda.api.entities.Member;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class PraiseManager {
    private final HashMap<String, Integer> praises = new HashMap<>();
    private final HashMap<String, Long> cooldowns = new HashMap<>();

    public void addPraise(Member member) {
        String id = member.getId();
        if(praises.containsKey(id))
            praises.put(id, praises.get(id) + 1);
        else
            praises.put(id, 1);
    }

    public void addCooldown(Member member) {
        String id = member.getId();
        long unix = TimeUtils.getUnixTime();
        long UNIX_24HRS = 86400;
        cooldowns.put(id, unix + UNIX_24HRS);
    }

    public boolean isOnCooldown(Member member) {
        String id = member.getId();
        if(!cooldowns.containsKey(id))
            return false;

        long cooldown = cooldowns.get(id);
        if(TimeUtils.getUnixTime() >= cooldown) {
            cooldowns.remove(id);
            return false;
        }

        return true;
    }

    public long getCooldown(Member member) {
        String id = member.getId();
        if(!cooldowns.containsKey(id))
            return -1;
        return cooldowns.get(id);
    }

    public void load(JSONObject json) {
        if(json.has("praiseSystem")) {
            JSONObject data = json.getJSONObject("praiseSystem");

            JSONArray praisesJSON = data.getJSONArray("praises");
            for(int i = 0; i < praisesJSON.length(); i++) {
                JSONObject userJSON = praisesJSON.getJSONObject(i);
                praises.put(userJSON.getString("userID"), userJSON.getInt("praises"));
            }

            JSONArray cooldownsJSON = data.getJSONArray("cooldowns");
            for(int i = 0; i < cooldownsJSON.length(); i++) {
                JSONObject userJSON = cooldownsJSON.getJSONObject(i);
                cooldowns.put(userJSON.getString("userID"), userJSON.getLong("cooldown"));
            }
        }
    }

    public JSONObject toJSON() {
        JSONArray praisesJSON = new JSONArray();
        for(String userID : praises.keySet()) {
            JSONObject userJSON = new JSONObject();
            userJSON.put("userID", userID);
            userJSON.put("praises", praises.get(userID));
            praisesJSON.put(userJSON);
        }

        JSONArray cooldownsJSON = new JSONArray();
        for(String userID : cooldowns.keySet()) {
            JSONObject userJSON = new JSONObject();
            userJSON.put("userID", userID);
            userJSON.put("cooldown", cooldowns.get(userID));
        }

        JSONObject data = new JSONObject();
        data.put("praises", praisesJSON);
        data.put("cooldowns", cooldownsJSON);
        return data;
    }
}
