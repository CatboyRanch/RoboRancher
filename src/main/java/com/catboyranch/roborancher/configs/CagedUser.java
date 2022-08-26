package com.catboyranch.roborancher.configs;

import com.catboyranch.roborancher.utils.FileUtils;
import com.catboyranch.roborancher.utils.RoleUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class CagedUser {
    private final File file;
    private final ArrayList<String> roles = new ArrayList<>();
    private final Member member;
    private final ServerConfig config;

    public CagedUser(ServerConfig config, Member member) {
        this.member = member;
        this.config = config;
        file = new File(String.format("%s/caged/%s.json", config.getServerPath(), member.getId()));
        if(!file.exists()) {
            JSONObject jsonFile = new JSONObject();
            JSONArray jsonRoles = new JSONArray();

            for(Role role : member.getRoles()) {
                roles.add(role.getId());
                jsonRoles.put(role.getId());
                RoleUtils.removeRole(member, role.getId());
            }
            jsonFile.put("roles", jsonRoles);
            FileUtils.saveString(file.getAbsolutePath(), jsonFile.toString(4));
        } else {
            JSONObject jsonFile = new JSONObject(FileUtils.loadString(file.getAbsolutePath()));
            JSONArray jsonRoles = jsonFile.getJSONArray("roles");
            for(int i = 0; i < jsonRoles.length(); i++) {
                roles.add(jsonRoles.getString(i));
            }
        }

        RoleUtils.addRole(member, config.getCagedRoleID());
    }

    public void doUncage() {
        file.delete();
        for(String roleID : roles)
            RoleUtils.addRole(member, roleID);
        RoleUtils.removeRole(member, config.getCagedRoleID());
    }
}
