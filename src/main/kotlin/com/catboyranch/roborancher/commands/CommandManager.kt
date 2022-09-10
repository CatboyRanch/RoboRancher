package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.ChannelUtils
import com.catboyranch.roborancher.RoboRancher
import com.catboyranch.roborancher.RoleUtils
import com.catboyranch.roborancher.Server
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*
import kotlin.collections.HashMap

class CommandManager(private val rancher: RoboRancher): ListenerAdapter() {
    private val commands = HashMap<String, CommandBase>()

    init {
        rancher.getJDA().addEventListener(this)
    }

    fun registerCommand(command: CommandBase) {
        commands[command.name] = command
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if(event.channelType == ChannelType.PRIVATE || event.author.isBot)
            return

        val member = event.member ?: return
        val server = rancher.getServer(event.guild.id) ?: return
        val cfg = server.config
        var msgContent = event.message.contentRaw

        if(cfg.getData().deleteFilter && !event.guildChannel.asTextChannel().isNSFW && !RoleUtils.hasAdminPermission(member) && !RoleUtils.hasRole(server, member, RoleUtils.RoleType.ADMIN)) {
            if(cfg.isFilterHard(msgContent) || cfg.isFilterSoft(msgContent))
                ChannelUtils.deleteMessage(event.message);
        }

        val prefix = cfg.getData().cmdPrefix
        if(!msgContent.startsWith(prefix))
            return

        msgContent = msgContent.replace(prefix, "");
        val argsRaw = msgContent.split(" ").toMutableList();

        val cmd = argsRaw.removeFirst()
        if (!commands.containsKey(cmd)) return

        val argsList = ArrayList<CommandArgument>()
        argsRaw.forEach {
            if(it != " ")
                argsList.add(CommandArgument(server, it))
        }
        val args = argsList.toTypedArray()
        val hasAdminPerm = RoleUtils.hasAdminPermission(member)
        val cmdBase = commands[cmd] ?: return
        var runCommand = false

        if(!hasAdminPerm) {
            cmdBase.allowedRoles.forEach {
                if(RoleUtils.hasRole(server, member, it))
                    runCommand = true
            }
        }

        if(runCommand || hasAdminPerm) {
            val result = object: CommandResult {
                val msg = event.message
                fun r(emoji: String) = msg.addReaction(Emoji.fromUnicode(emoji)).queue()

                override fun success() = r("✅")
                override fun successQuiet() { }
                override fun error(message: String) {
                    r("❌")
                    msg.reply(message).queue()
                }
            }
            cmdBase.run(cmd, server, event, result, *args)
        }
    }
}

abstract class CommandBase(private val rancher: RoboRancher) {
    abstract val name: String
    abstract val description: String
    abstract val allowedRoles: Array<RoleUtils.RoleType>

    abstract fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument)
}

interface CommandResult {
    fun success()
    fun successQuiet()
    fun error(message: String)
}