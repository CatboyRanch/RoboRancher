package com.catboyranch.roborancher.utils;

import com.catboyranch.roborancher.Server;
import net.dv8tion.jda.api.entities.Member;

public class MemberUtils {
    public static Member getMemberById(Server server, String id) {
        return server.getGuild().getMemberById(id);
    }
}
