package com.catboyranch.roborancher.configs;

import lombok.Getter;
import org.json.JSONObject;

public class RoleMessageReaction {
    @Getter
    private final String emoji;
    @Getter
    private final String roleID;

    public RoleMessageReaction(String emoji, String roleID) {
        this.emoji = emoji;
        this.roleID = roleID;
    }

    public RoleMessageReaction(JSONObject json) {
        this.emoji = json.getString("emoji");
        this.roleID = json.getString("roleID");
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("emoji", emoji);
        json.put("roleID", roleID);
        return json;
    }
}
