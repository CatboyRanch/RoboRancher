package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.*
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class CEdit(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "edit"
    override val description = "Edit messages sent by me!"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        if(args.size < 2) {
            result.error("Not enough arguments!")
            return
        }
        val messageID = args[0].text
        val prefix = server.config.getData().cmdPrefix
        val newMessage = event.message.contentRaw.replace("$prefix$cmd $messageID", "")

        ChannelUtils.getMessage(server, messageID, object: IFunction {
            override fun run(vararg args: Any) {
                if(args.isEmpty()) {
                    result.error("Could not find message!")
                    return
                }
                val msg = args[0] as Message
                msg.editMessage(newMessage).queue()
                result.success()
            }
        })
    }
}