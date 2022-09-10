package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.ChannelUtils
import com.catboyranch.roborancher.RoboRancher
import com.catboyranch.roborancher.RoleUtils
import com.catboyranch.roborancher.Server
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class CSay(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "say"
    override val description = "Speak catboy!"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        val message = event.message.contentRaw.replace(server.config.getData().cmdPrefix + name, "")
        ChannelUtils.deleteMessage(event.message)
        event.channel.sendMessage(message).queue()
        result.successQuiet()
    }
}