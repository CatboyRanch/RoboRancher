package com.catboyranch.roborancher.managers;

import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.utils.ChannelUtils;
import com.catboyranch.roborancher.utils.RoleUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class RoleMessageManager {
    private final Server server;
    private final HashMap<String, RoleMessage> roleMessages = new HashMap<>();

    public RoleMessageManager(Server server) {
        this.server = server;
    }

    public void load(JSONObject json) {
        if(json.has("roleMessages")) {
            JSONArray roleMessagesJSON = json.getJSONArray("roleMessages");
            for (int i = 0; i < roleMessagesJSON.length(); i++) {
                JSONObject rmJSON = roleMessagesJSON.getJSONObject(i);
                roleMessages.put(rmJSON.getString("id"), new RoleMessage(rmJSON));
            }
        }
    }

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
                for(String emoji : rm.getReactions().keySet())
                    message.addReaction(Emoji.fromFormatted(emoji)).queue((success) -> {}, (failure) -> {
                        //Emoji didnt work, remove it
                        System.out.printf("Could not add emoji %s to message %s!\n", emoji, messageID);
                        rm.removeReaction(emoji);
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

        HashMap<String, String> map = roleMessages.get(messageID).getReactions();
        for(String currentEmoji : map.keySet()) {
            if(currentEmoji.equals(emoji.getFormatted())) {
                return RoleUtils.getRole(map.get(currentEmoji), server);
            }
        }
        return null;
    }

    public JSONArray toJSON() {
        JSONArray roleMessagesJSON = new JSONArray();
        for(String messageID : roleMessages.keySet()) {
            RoleMessage rm = roleMessages.get(messageID);
            if(!rm.getReactions().isEmpty())
                roleMessagesJSON.put(rm.toJSON());
        }
        return roleMessagesJSON;
    }
}
