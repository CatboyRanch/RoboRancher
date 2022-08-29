package com.catboyranch.roborancher;

import com.catboyranch.roborancher.utils.FileUtils;
import com.catboyranch.roborancher.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ServerConfig {
    private final Server server;
    @Setter
    @Getter
    private String cmdPrefix;
    @Setter
    @Getter
    private String cagedRoleID;
    @Setter
    @Getter
    private String adminRole;
    @Setter
    @Getter
    private String modRole;
    @Setter
    @Getter
    private String memberRole;
    @Setter
    @Getter
    private boolean deleteFilter;
    @Getter
    @Setter
    private long praiseCooldown;

    @Getter
    private final ArrayList<String> softFilter = new ArrayList<>();
    @Getter
    private final ArrayList<String> hardFilter = new ArrayList<>();

    private final String configPath;
    private final JSONObject defaults = new JSONObject(FileUtils.loadFromJar("RoboRancher/cfg/server_defaults/config.json"));


    public ServerConfig(Server server) {
        this.server = server;
        configPath = String.format("%s/RoboRancher/servers/%s.json", FileUtils.getJarDirectory(), server.getGuild().getId());
        if (new File(configPath).exists()) {
            //Config exists
            JSONObject configJSON = new JSONObject(FileUtils.loadString(configPath));
            load(configJSON);
        } else {
            //Config does not exist, load defaults
            //TODO: Change RoboRancher path, thats dumb
            load(defaults);
        }
    }

    private void load(JSONObject json) {
        cmdPrefix = getStringOrDefault(json, "cmdprefix");
        cagedRoleID = getStringOrDefault(json, "cageRole");
        adminRole = getStringOrDefault(json, "adminRole");
        modRole = getStringOrDefault(json, "modRole");
        memberRole = getStringOrDefault(json, "memberRole");
        deleteFilter = getBoolOrDefault(json, "deleteFilter");
        praiseCooldown = getLongOrDefault(json, "praiseCooldown");

        JSONArray softWordsJSON = getJSONArrayOrDefault(json, "filterWordsSoft");
        for(int i = 0; i < softWordsJSON.length(); i++)
            softFilter.add(softWordsJSON.getString(i));

        JSONArray hardWordsJSON = getJSONArrayOrDefault(json, "filterWordsHard");
        for(int i = 0; i < hardWordsJSON.length(); i++)
            hardFilter.add(hardWordsJSON.getString(i));

        server.getCageManager().load(json);
        server.getRuleManager().load(json);
        server.getRoleMessageManager().load(json);
        server.getPraiseManager().load(json);
    }

    private JSONArray getJSONArrayOrDefault(JSONObject json, String key) {
        return json.has(key) ? json.getJSONArray(key) : defaults.getJSONArray(key);
    }

    private String getStringOrDefault(JSONObject json, String key) {
        return json.has(key) ? json.getString(key) : defaults.getString(key);
    }

    private boolean getBoolOrDefault(JSONObject json, String key) {
        return json.has(key) ? json.getBoolean(key) : defaults.getBoolean(key);
    }

    private long getLongOrDefault(JSONObject json, String key) {
        return json.has(key) ? json.getLong(key) : defaults.getLong(key);
    }

    public void save() {
        JSONObject json = new JSONObject();
        json.put("cmdprefix", cmdPrefix);
        json.put("cageRole", cagedRoleID);
        json.put("adminRole", adminRole);
        json.put("modRole", modRole);
        json.put("memberRole", memberRole);
        json.put("deleteFilter", deleteFilter);
        json.put("praiseCooldown", praiseCooldown);

        json.put("filterWordsSoft", Utils.getJSONArrayFromArray(softFilter));
        json.put("filterWordsHard", Utils.getJSONArrayFromArray(hardFilter));

        json.put("roleMessages", server.getRoleMessageManager().toJSON());
        json.put("rules", server.getRuleManager().toJSON());
        json.put("cagedUsers", server.getCageManager().toJSON());
        json.put("praiseSystem", server.getPraiseManager().toJSON());

        FileUtils.saveString(configPath, json.toString(4));
    }

    public boolean isFilterHard(String string) {
        for(String word : hardFilter) {
            if(string.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFilterSoft(String string) {
        for (String word : softFilter) {
            final int wordCutoff = 4; //if someone says cum. it will be deleted, if its a word like cumbersome it will stay
            if (string.contains(" " + word + " ") || string.contains(" " + word) || string.contains(word + " ") || string.equals(word) || (string.contains(word) && string.length() <= word.length() + wordCutoff)) {
                return true;
            }
        }
        return false;
    }

    public void addSoftWord(String word) {
        if(!softFilter.contains(word))
            softFilter.add(word);
    }

    public void removeSoftWord(String word) {
        softFilter.remove(word);
    }

    public void addHardWord(String word) {
        if(!hardFilter.contains(word))
            hardFilter.add(word);
    }

    public void removeHardWord(String word) {
        hardFilter.remove(word);
    }
}
