package com.catboyranch.roborancher.events;

import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.utils.RoleUtils;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEvents extends ListenerAdapter {
    private final RoboRancher rancher;

    public MessageEvents(RoboRancher rancher) {
        this.rancher = rancher;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getMember() == null)
            return;

        Server server = rancher.getServer(event.getGuild().getId());
        Role role = server.getRoleMessageManager().getRoleForMessageEmoji(event.getMessageId(), event.getEmoji());
        if(role != null)
            RoleUtils.addRole(event.getMember(), role);
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if(event.getMember() == null)
            return;

        Server server = rancher.getServer(event.getGuild().getId());
        Role role = server.getRoleMessageManager().getRoleForMessageEmoji(event.getMessageId(), event.getEmoji());
        if(role != null)
            RoleUtils.removeRole(event.getMember(), role);
    }
}
