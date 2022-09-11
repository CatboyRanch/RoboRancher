package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent


class CInfo(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "info"
    override val description = "Get info for user"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN, RoleUtils.RoleType.MODERATOR)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        if(args.isEmpty()) {
            result.error("Tag someone or supply a user id!");
            return;
        }

        val member = if (args[0].type == CommandArgument.TYPE.USER_TAG) args[0].getMember() else MemberUtils.getMember(server, args[0].text)
        if(member == null) {
            result.error("Member not found!")
            return
        }
        val boosted = member.timeBoosted?.let { TimestampType.LONG_DATE_SHORT_TIME.format(it) }  ?: "Not boosted"
        val message = """
            User info:
            ID: ${member.id}
            Current username: ${member.user.name}#${member.user.discriminator} (${TimestampType.LONG_DATE_SHORT_TIME.formatNow()})
            Mention: ${member.asMention}
            Account created: ${TimestampType.LONG_DATE_SHORT_TIME.format(member.timeCreated)}
            Time joined: ${TimestampType.LONG_DATE_SHORT_TIME.format(member.timeJoined)}
            Time boosted: $boosted 
        """.trimIndent()
        event.channel.sendMessage(message).queue()
        result.success()
    }
}