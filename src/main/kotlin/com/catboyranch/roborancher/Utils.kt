package com.catboyranch.roborancher

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
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

object RoleUtils {
    enum class RoleType { ADMIN, MODERATOR, MEMBER, EVERYONE }

    fun getRole(server: Server, roleID: String): Role? {
        server.guild.roles.forEach { if(it.id == roleID) return it }
        return null
    }

    fun getRoleName(server: Server, roleID: String): String {
        return getRole(server, roleID)?.name ?: "null"
    }

    fun hasRole(member: Member, roleID: String): Boolean {
        member.roles.forEach { if(it.id == roleID) return true }
        return false
    }

    fun hasRole(server: Server, member: Member, roleType: RoleType): Boolean {
        val cfgData = server.config.getData()
        val id: String = when(roleType) {
            RoleType.ADMIN -> cfgData.adminRole
            RoleType.MODERATOR -> cfgData.modRole
            RoleType.MEMBER -> cfgData.memberRole
            RoleType.EVERYONE -> return true
        }
        return hasRole(member, id)
    }

    fun hasAdminPermission(member: Member): Boolean = member.hasPermission(Permission.ADMINISTRATOR)

    fun addRole(member: Member, role: Role): Boolean {
        return try {
            member.guild.addRoleToMember(member, role).queue()
            true
        } catch(exception: Throwable) {
            false
        }
    }

    fun addRole(member: Member, roleID: String): Boolean {
        val role = member.guild.getRoleById(roleID)
        if(role == null)
            return false
        else
            addRole(member, role)
        return true
    }
}