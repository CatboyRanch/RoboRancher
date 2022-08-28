package com.catboyranch.roborancher.configs;

import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.utils.ChannelUtils;
import com.catboyranch.roborancher.utils.FileUtils;
import com.catboyranch.roborancher.utils.RoleUtils;
import com.catboyranch.roborancher.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerConfig {
    private final Server server;
    @Getter
    private final String serverPath;
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
    private final ArrayList<String> softFilter = new ArrayList<>();
    @Getter
    private final ArrayList<String> hardFilter = new ArrayList<>();

    @Getter
    private RuleFile ruleFile;

    private final HashMap<String, RoleMessage> roleMessages = new HashMap<>();
    private final HashMap<String, CagedUser> cagedUsers = new HashMap<>();

    public ServerConfig(Server server) {
        this.server = server;
        serverPath = String.format("%s/RoboRancher/servers/%s/", FileUtils.getJarDirectory(), server.getGuild().getId());
        FileUtils.createFolder(serverPath);
        FileUtils.createFolder(serverPath + "/caged/");
        String configPath = serverPath + "config.json";
        if (new File(configPath).exists()) {
            //Config exists
            JSONObject configJSON = new JSONObject(FileUtils.loadString(configPath));
            load(configJSON);
        } else {
            //Config does not exist, load defaults
            //TODO: Change RoboRancher path, thats dumb
            JSONObject configJSON = new JSONObject(FileUtils.loadFromJar("RoboRancher/cfg/server_defaults/config.json"));
            load(configJSON);
        }
    }

    private void load(JSONObject json) {
        cmdPrefix = json.getString("cmdprefix");
        cagedRoleID = json.getString("cageRole");
        adminRole = json.getString("adminRole");
        modRole = json.getString("modRole");
        memberRole = json.getString("memberRole");
        deleteFilter = json.getBoolean("deleteFilter");

        JSONArray softWordsJSON = json.getJSONArray("filterWordsSoft");
        for(int i = 0; i < softWordsJSON.length(); i++)
            softFilter.add(softWordsJSON.getString(i));

        JSONArray hardWordsJSON = json.getJSONArray("filterWordsHard");
        for(int i = 0; i < hardWordsJSON.length(); i++)
            hardFilter.add(hardWordsJSON.getString(i));

        if(json.has("roleMessages")) {
            JSONArray roleMessagesJSON = json.getJSONArray("roleMessages");
            for (int i = 0; i < roleMessagesJSON.length(); i++) {
                JSONObject rmJSON = roleMessagesJSON.getJSONObject(i);
                roleMessages.put(rmJSON.getString("id"), new RoleMessage(rmJSON));
            }
        }

        ruleFile = json.has("rules") ? new RuleFile(json.getJSONArray("rules")) : new RuleFile();
    }

    public void save() {
        JSONObject json = new JSONObject();
        json.put("cmdprefix", cmdPrefix);
        json.put("cageRole", cagedRoleID);
        json.put("adminRole", adminRole);
        json.put("modRole", modRole);
        json.put("memberRole", memberRole);
        json.put("deleteFilter", deleteFilter);

        json.put("filterWordsSoft", Utils.getJSONArrayFromArray(softFilter));
        json.put("filterWordsHard", Utils.getJSONArrayFromArray(hardFilter));

        JSONArray roleMessagesJSON = new JSONArray();
        for(String messageID : roleMessages.keySet()) {
            RoleMessage rm = roleMessages.get(messageID);
            if(!rm.getReactions().isEmpty())
                roleMessagesJSON.put(rm.toJSON());
        }
        json.put("roleMessages", roleMessagesJSON);
        json.put("rules", ruleFile.toJSON());

        FileUtils.saveString(serverPath + "config.json", json.toString(4));
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

    public void cageUser(Member member) {
        if(cagedUsers.containsKey(member.getId()))
            return;

        cagedUsers.put(member.getId(), new CagedUser(this, member));
    }

    public void uncageUser(Member member) {
        String id = member.getId();
        if(!cagedUsers.containsKey(id))
            return;

        cagedUsers.get(id).doUncage();
        cagedUsers.remove(id);
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

    //No validation
    public void addRoleMessageEmoji(Message message, Emoji emoji, Role role) {
        String messageID = message.getId();
        if(!roleMessages.containsKey(messageID))
            roleMessages.put(messageID, new RoleMessage(messageID));
        roleMessages.get(messageID).addReaction(emoji.getFormatted(), role.getId());
    }

    public void ensureRoleMessageEmojis() {
        for(String messageID : roleMessages.keySet()) {
            ChannelUtils.getMessage(server, messageID, objects -> {
                if(objects.length == 0) {
                    System.out.println("Error ensuring role message emoji for message id " + messageID);
                    roleMessages.remove(messageID);
                    return;
                }
                RoleMessage rm = roleMessages.get(messageID);
                Message message = (Message)objects[0];
                for(RoleMessageReaction reaction : rm.getReactions())
                    message.addReaction(Emoji.fromFormatted(reaction.getEmoji())).queue((success) -> {}, (failure) -> {
                        //Emoji didnt work, remove it
                        System.out.printf("Could not add emoji %s to message %s!\n", reaction.getEmoji(), messageID);
                        rm.removeReaction(reaction.getEmoji());
                    });
            });
        }
    }

    public void removeRoleMessageEmoji(Message message, Emoji emoji) {
        String messageID = message.getId();
        if(!roleMessages.containsKey(messageID))
            return;
        RoleMessage rm = roleMessages.get(messageID);
        rm.removeReaction(emoji.getFormatted());
        if(rm.shouldRemove()) {
            message.removeReaction(emoji).queue();
            roleMessages.remove(messageID);
        }
    }

    public Role getRoleForMessageEmoji(String messageID, Emoji emoji) {
        if(!roleMessages.containsKey(messageID))
            return null;

        for(RoleMessageReaction r : roleMessages.get(messageID).getReactions()) {
            if(r.getEmoji().equals(emoji.getFormatted())) {
                return RoleUtils.getRole(r.getRoleID(), server);
            }
        }
        return null;
    }
}
