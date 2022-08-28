package com.catboyranch.roborancher.events;

import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.configs.ServerConfig;
import com.catboyranch.roborancher.utils.RoleUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinListener extends ListenerAdapter {
    private final RoboRancher rancher;

    public JoinListener(RoboRancher rancher) {
        this.rancher = rancher;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        ServerConfig cfg = rancher.getServer(event.getGuild().getId()).getConfig();
        if(cfg.isCaged(member))
            RoleUtils.addRole(member, cfg.getCagedRoleID());
    }

}
