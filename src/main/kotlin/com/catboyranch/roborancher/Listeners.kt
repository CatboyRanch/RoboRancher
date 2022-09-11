package com.catboyranch.roborancher

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class JoinListener(private val rancher: RoboRancher): ListenerAdapter() {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        rancher.getServer(event.guild.id)?.cageManager?.cage(event.member)
    }
}

class MessageListener(private val rancher: RoboRancher): ListenerAdapter() {
    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        val member = event.member ?: return
        val server = rancher.getServer(event.guild.id) ?: return
        val role = server.roleMessageManager.getRoleForMessageEmoji(event.messageId, event.emoji)
        if(role != null)
            RoleUtils.addRole(member, role)
    }

    override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
        val member = event.member ?: return
        val server = rancher.getServer(event.guild.id) ?: return
        val role = server.roleMessageManager.getRoleForMessageEmoji(event.messageId, event.emoji)
        if(role != null)
            RoleUtils.removeRole(member, role)
    }
}