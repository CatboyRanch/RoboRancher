package com.catboyranch.roborancher.managers;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class RoleMessage {
    @Getter
    private final String id;
    @Getter
    private final HashMap</*Emoji*/String, /*RoleID*/String> reactions = new HashMap<>();

    public RoleMessage(String id) {
        this.id = id;
    }

    public RoleMessage(JSONObject json) {
        this.id = json.getString("id");
        JSONArray reactionsJSON = json.getJSONArray("reactions");
        for(int i = 0; i < reactionsJSON.length(); i++) {
            JSONObject reactionJSON = reactionsJSON.getJSONObject(i);
            String rEmoji = reactionJSON.getString("emoji");
            String rRole = reactionJSON.getString("roleID");
            reactions.put(rEmoji, rRole);
        }
    }

    public void addReaction(String emoji, String roleID) {
        reactions.put(emoji, roleID);
    }

    public void removeReaction(String emoji) {
        reactions.remove(emoji);
    }

    public boolean shouldRemove() {
        return reactions.isEmpty();
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        JSONArray reactionsJSON = new JSONArray();
        for(String emoji : reactions.keySet()) {
            JSONObject reaction = new JSONObject();
            reaction.put("emoji", emoji);
            reaction.put("roleID", reactions.get(emoji));
            reactionsJSON.put(reaction);
        }
        json.put("reactions", reactionsJSON);
        return json;
    }
}
