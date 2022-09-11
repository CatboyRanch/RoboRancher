package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.ChannelUtils
import com.catboyranch.roborancher.RoboRancher
import com.catboyranch.roborancher.RoleUtils
import com.catboyranch.roborancher.Server
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class CRule(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "rule"
    override val description = "Show a specific rule"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN, RoleUtils.RoleType.MODERATOR, RoleUtils.RoleType.MEMBER)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        if(args.isEmpty()) {
            result.error("Please add an index for whichever rule you want to quote!")
            return
        }

        val rm = server.ruleManager
        var index = args[0].text.toIntOrNull()
        if(index == null || index <= 0 || index > rm.getRules().size) {
            result.error("bad index!")
            return
        }

        val builder = EmbedBuilder()
        builder.setTitle("Rule $index")
        builder.setFooter("Requested by ${event.member?.effectiveName}")
        index--
        builder.setDescription(rm.getRules()[index])
        ChannelUtils.deleteMessage(event.message)
        event.channel.sendMessage(MessageBuilder(builder).build()).queue()
        result.successQuiet()
    }
}