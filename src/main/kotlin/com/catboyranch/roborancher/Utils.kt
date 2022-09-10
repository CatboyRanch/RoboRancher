package com.catboyranch.roborancher

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

interface IFunction {
    fun run(vararg args: Any)
}

object Utils {
    fun cutFirstChar(string: String):String {
        return string.substring(1)
    }

    fun getRandom(min: Int, max: Int): Int {
        return ThreadLocalRandom.current().nextInt(min, max + 1)
    }
}

object FileUtils {
    private val DEFAULT_CHARSET = StandardCharsets.UTF_8
    private var jarDirectory: String

    init {
        val location = RoboRancher::class.java.protectionDomain.codeSource.location
        val filename = File(location.path).name
        jarDirectory = File(location.toURI()).path.replace(filename, "")
    }

    fun getJarDirectory(): String = jarDirectory

}

object ChannelUtils {
    fun sendPrivateMessage(member: Member, message: String) = member.user.openPrivateChannel().queue { it.sendMessage(message).queue() }

    fun getChannel(guild: Guild, channelID: String): MessageChannel? = guild.getTextChannelById(channelID)

    fun getMessage(server: Server, messageID: String, onComplete: IFunction) {
        var found = false
        var checked = 0
        val size = server.guild.textChannels.size
        for(channel in server.guild.textChannels) {
            if(found)
                break
            channel.retrieveMessageById(messageID).queue {
                found = true
                onComplete.run(it)
            }
            channel.retrieveMessageById(messageID).queue({
                found = true
                onComplete.run(it)
            }) {
               if(checked >= size && !found)
                   onComplete.run()
                checked++
            }
        }
    }

    fun getChannelTag(channelID: String, server: Server): String = server.guild.getGuildChannelById(channelID)?.asMention ?: channelID

    fun deleteMessage(message: Message) = message.delete().queue()

    fun isChannel(server: Server, id: String): Boolean {
        server.guild.channels.forEach { if(it.id == id) return true }
        return false
    }

}