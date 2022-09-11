package com.catboyranch.roborancher.managers

import com.catboyranch.roborancher.ChannelUtils
import com.catboyranch.roborancher.IFunction
import com.catboyranch.roborancher.RoleUtils
import com.catboyranch.roborancher.Server
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.emoji.Emoji

class RoleMessageManager(private val server: Server) {
    val rm = server.config.getData().roleMessages

    fun addRoleMessageEmoji(message: Message, emoji: Emoji, role: Role) {
        val messageID = message.id
        if(!rm.containsKey(messageID))
            rm[messageID] = HashMap()
        rm[messageID]?.set(emoji.formatted, role.id)
        message.addReaction(emoji).queue()
    }

    fun removeRoleMessageEmoji(message: Message, emoji: Emoji) {
        val messageID = message.id
        val map = rm[messageID] ?: return
        message.removeReaction(emoji).queue()
        map.remove(emoji.formatted)
        if(map.isEmpty())
            rm.remove(messageID)
    }

    fun getRoleForMessageEmoji(messageID: String, emoji: Emoji): Role? = rm[messageID]?.get(emoji.formatted)?.let { RoleUtils.getRole(server, it) }

    fun ensureRoleMessageEmojis() {
        rm.forEach { (messageID, roleMessage) ->
            if(roleMessage.isEmpty()) {
                //Role message is empty in data, just delete it and return
                rm.remove(messageID)
                return
            }
            ChannelUtils.getMessage(server, messageID, object: IFunction {
                override fun run(vararg args: Any) {
                    if(args.isEmpty()) {
                        println("Error ensuring role message emoji for message id $messageID")
                        rm.remove(messageID)
                        return
                    }
                    val message = args[0] as Message
                    rm[messageID]?.forEach { (emoji, _) ->
                        message.addReaction(Emoji.fromFormatted(emoji)).queue({ }, {
                            println("Could not add emoji $emoji to message $messageID")
                            rm[messageID]?.remove(emoji)
                        })
                    }
                }

            })
        }
    }
}