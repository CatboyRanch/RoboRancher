package com.catboyranch.roborancher.managers

import com.catboyranch.roborancher.RoleUtils
import com.catboyranch.roborancher.Server
import net.dv8tion.jda.api.entities.Member

class CageManager(private val server: Server) {
    private val data = server.config.getData()

    fun cage(member: Member) {
        if(isCaged(member)) return
        //Collect roles
        val roles = ArrayList<String>()
        member.roles.forEach { roles.add(it.id) }
        //Update user
        roles.forEach { RoleUtils.removeRole(member, server, it) }
        RoleUtils.addRole(member, data.cagedRoleID)
        //Update data
        data.cagedUsers[member.id] = roles
    }

    fun uncage(member: Member) {
        if(!isCaged(member)) return
        //Update user
        RoleUtils.removeRole(member, server, data.cagedRoleID)
        data.cagedUsers[member.id]?.forEach { RoleUtils.addRole(member, it) }
        //Update data
        data.cagedUsers.remove(member.id)
    }

    fun isCaged(member: Member): Boolean = data.cagedUsers.containsKey(member.id)
}