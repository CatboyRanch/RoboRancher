package com.catboyranch.roborancher.managers;

import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.utils.RoleUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CageManager {
    private final Server server;
    private final HashMap<String, ArrayList<String>> cagedUsers = new HashMap<>();

    public CageManager(Server server) {
        this.server = server;
    }

    public void load(JSONObject json) {
        if(json.has("cagedUsers")) {
            JSONArray cagedUsersJSON = json.getJSONArray("cagedUsers");
            for(int i = 0; i < cagedUsersJSON.length(); i++) {
                JSONObject cagedUserJSON = cagedUsersJSON.getJSONObject(i);
                ArrayList<String> cagedUserRoles = new ArrayList<>();
                JSONArray rolesJSON = cagedUserJSON.getJSONArray("roles");
                for(int i2 = 0; i2 < rolesJSON.length(); i2++)
                    cagedUserRoles.add(rolesJSON.getString(i2));
                cagedUsers.put(cagedUserJSON.getString("userID"), cagedUserRoles);
            }
        }
    }

    public JSONArray toJSON() {
        JSONArray cagedUsersJSON = new JSONArray();
        for(String userID : cagedUsers.keySet()) {
            JSONObject cagedUserJSON = new JSONObject();
            cagedUserJSON.put("userID", userID);
            cagedUserJSON.put("roles", cagedUsers.get(userID));
            cagedUsersJSON.put(cagedUserJSON);
        }
        return cagedUsersJSON;
    }

    public boolean isCaged(Member member) {
        return cagedUsers.containsKey(member.getId());
    }

    public void cageUser(Member member) {
        if(cagedUsers.containsKey(member.getId()))
            return;

        ArrayList<String> roles = new ArrayList<>();
        for(Role role : member.getRoles()) {
            roles.add(role.getId());
            RoleUtils.removeRole(member, role);
        }
        RoleUtils.addRole(member, server.getConfig().getCagedRoleID());
        cagedUsers.put(member.getId(), roles);
    }

    public void uncageUser(Member member) {
        String id = member.getId();
        if(!cagedUsers.containsKey(id))
            return;

        RoleUtils.removeRole(member, server.getConfig().getCagedRoleID());
        for(String roleID : cagedUsers.get(id))
            RoleUtils.addRole(member, roleID);
        cagedUsers.remove(id);
    }
}
