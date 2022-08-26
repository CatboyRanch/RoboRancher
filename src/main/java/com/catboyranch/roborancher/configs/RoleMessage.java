package com.catboyranch.roborancher.configs;

import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoleMessage {
    @Getter
    private final String id;
    @Getter
    private final ArrayList<RoleMessageReaction> reactions = new ArrayList<>();

    public RoleMessage(String id) {
        this.id = id;
    }

    public RoleMessage(JSONObject json) {
        this.id = json.getString("id");
        JSONArray reactionsJSON = json.getJSONArray("reactions");
        for(int i = 0; i < reactionsJSON.length(); i++) {
            reactions.add(new RoleMessageReaction(reactionsJSON.getJSONObject(i)));
        }
    }

    public void addReaction(String emoji, String roleID) {
        reactions.add(new RoleMessageReaction(emoji, roleID));
    }

    public void removeReaction(String emoji) {
        reactions.removeIf(reaction -> reaction.getEmoji().equals(emoji));
    }

    public boolean shouldRemove() {
        return reactions.isEmpty();
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        JSONArray reactionsJSON = new JSONArray();
        for(RoleMessageReaction reaction : reactions) {
            reactionsJSON.put(reaction.toJSON());
        }
        json.put("reactions", reactionsJSON);
        return json;
    }
}
