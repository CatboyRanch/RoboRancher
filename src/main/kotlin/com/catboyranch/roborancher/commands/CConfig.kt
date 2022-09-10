package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.RoboRancher
import com.catboyranch.roborancher.RoleUtils
import com.catboyranch.roborancher.Server
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class CConfig(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "config"
    override val description = "Change config settings"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        val cfg = server.config
        val data = cfg.getData()

        if(args.isEmpty() || args[0].text == "help") {
            val p = data.cmdPrefix
            val message = """
                :desktop: General commands: :desktop:
                ${p}config help
                ${p}config save
                ${p}config prefix <prefix> (current: $p)
                ${p}config setrole <admin/mod/member/caged> <role-id> (current: admin(${data.adminRole}), moderator(${data.modRole}), member(${data.memberRole})
                ${p}config praisecooldown <unix-seconds> (current: ${data.praiseCooldown})
                
                :desktop: Filter commands: :desktop:
                ${p}config filter <true/false> (current: ${data.deleteFilter})
                ${p}config addFilter <soft/hard> <word> (current: soft:||${data.softFilter}||, hard:||${data.hardFilter}||),
                ${p}config delFilter <soft/hard> <word>
                
                :desktop: Rules commands: :desktop:
                ${p}config rules list
                ${p}config rules <add/remove> <index> <text>
                ${p}config rules send <id>
                
                :desktop: Role message commands: :desktop:
                ${p}config rolemsg add <message-id> <emoji> <role>
                ${p}config rolemsg remove <message-id> <emoji>
            """.trimIndent()
            event.channel.sendMessage(message).queue()
            result.success()
            return
        }

    }
}