package com.catboyranch.roborancher.utils;

import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.configs.RoleType;
import com.catboyranch.roborancher.configs.ServerConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

public class RoleUtils {
    public static String getRoleName(String roleID, Server server) {
        Role role = getRole(roleID, server);
        if(role != null)
            return role.getName();
        return null;
    }

    public static Role getRole(String roleID, Server server) {
        for(Role role : server.getGuild().getRoles())
            if(role.getId().equals(roleID))
                return role;
        return null;
    }

    public static boolean hasRole(Server server, Member member, RoleType role) {
        ServerConfig cfg = server.getConfig();
        String id = null;
        switch(role) {
            case ADMIN -> id = cfg.getAdminRole();
            case MODERATOR -> id = cfg.getModRole();
            case MEMBER -> id = cfg.getMemberRole();
            case EVERYONE -> {return true;}
        }
        if(id != null)
            return hasRole(member, id);
        return false;
    }

    public static boolean hasAdminPermission(@NotNull Member member) {
        return member.hasPermission(Permission.ADMINISTRATOR);
    }

    public static boolean addRole(Member member, String roleID) {
        Guild guild = member.getGuild();
        Role role = guild.getRoleById(roleID);
        return addRole(member, role);
    }

    public static boolean addRole(Member member, Role role) {
        Guild guild = member.getGuild();
        if(role == null)
            return false;
        guild.addRoleToMember(member, role).queue();
        return true;
    }

    public static boolean addRole(Guild guild, String memberID, String roleID) {
        Member member = guild.getMemberById(memberID);
        if(member == null)
            return false;
        return addRole(member, roleID);
    }

    public static boolean removeRole(Member member, String roleID) {
        Guild guild = member.getGuild();
        Role role = guild.getRoleById(roleID);
        return removeRole(member, role);
    }

    public static boolean removeRole(Member member, Role role) {
        Guild guild = member.getGuild();
        if(role == null)
            return false;
        guild.removeRoleFromMember(member, role).queue();
        return true;
    }

    public static boolean removeRole(Guild guild, String memberID, String roleID) {
        Member member = guild.getMemberById(memberID);
        if(member == null)
            return false;
        return removeRole(member, roleID);
    }

    public static boolean isRole(Server server, String id) {
        return getRole(id, server) != null;
    }

    public static boolean hasRole(Member member, String role) {
        for(Role r : member.getRoles()) {
            if(r.getId().equals(role))
                return true;
        }
        return false;
    }
}
