package com.catboyranch.roborancher.events;

import com.catboyranch.roborancher.RoboRancher;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;

public class JoinListener extends ListenerAdapter {
    private final RoboRancher rancher;

    public JoinListener(RoboRancher rancher) {
        this.rancher = rancher;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        File f = new File("servers/" + event.getGuild().getId() + "/caged/" + event.getMember().getId() + ".json");
        if(f.exists()) {
            String role = rancher.getServer(event.getGuild().getId()).getConfig().getCagedRoleID();
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(role)).queue();
        }
    }

}
