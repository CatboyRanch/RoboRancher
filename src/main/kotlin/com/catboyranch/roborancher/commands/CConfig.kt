package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color
import java.time.Instant

class CConfig(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "config"
    override val description = "Change config settings"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        val cfg = server.config
        val data = cfg.getData()

        if(args.isEmpty() || args[0].text == "help") {
            val p = data.cmdPrefix
            val adminName = RoleUtils.getRoleName(server, data.adminRole)
            val modName = RoleUtils.getRoleName(server, data.modRole)
            val memberName = RoleUtils.getRoleName(server, data.memberRole)
            val cagedName = RoleUtils.getRoleName(server, data.cagedRoleID)

            val message = """
                :desktop: General commands: :desktop:
                ${p}config help
                ${p}config save
                ${p}config prefix <prefix> (current: $p)
                ${p}config setrole <admin/mod/member/caged> <role-id> (current: admin($adminName), moderator($modName), member($memberName), caged($cagedName))
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

        when(args[0].text) {
            "prefix" -> data.cmdPrefix = args[1].text
            "setrole" -> {
                val roleType = args[1].text
                val roleID = args[2].text
                if(!RoleUtils.isRole(server, roleID)) {
                    result.error("Role not found!")
                    return
                }

                when(roleType) {
                    "admin" -> data.adminRole = roleID
                    "mod", "moderator" -> data.modRole = roleID
                    "member" -> data.memberRole = roleID
                    "caged" -> data.cagedRoleID = roleID
                    else -> {
                        result.error("Bad role type!")
                        return
                    }
                }
                result.success()
                return
            }
            "praiseCooldown" -> {
                val seconds = args[1].text.toLongOrNull()
                if(seconds == null) {
                    result.error("Input must be in seconds!")
                    return
                }
                data.praiseCooldown = seconds
            }
            "filter" -> {
                val bool = args[1].text.toBooleanStrictOrNull()
                if(bool == null) {
                    result.error("Input must be either true or false!")
                    return
                }
                data.deleteFilter = bool
            }
            "addFilter" -> {
                when(args[1].text) {
                    "soft" -> if(!data.softFilter.contains(args[2].text)) data.softFilter.add(args[2].text)
                    "hard" -> if(!data.hardFilter.contains(args[2].text)) data.hardFilter.add(args[2].text)
                    else -> {
                        result.error("Input must be soft or hard!")
                        return
                    }
                }
            }
            "delFilter" -> {
                when(args[1].text) {
                    "soft" -> data.softFilter.remove(args[2].text)
                    "hard" -> data.hardFilter.remove(args[2].text)
                    else -> {
                        result.error("Input must be soft or hard!")
                        return
                    }
                }
            }
            "rules" -> {
                val rm = server.ruleManager
                when(args[1].text) {
                    "list" -> {
                        var message = "Rules: (Remember, arrays start at 0! :nerd_face:\n"
                        rm.getRules().forEachIndexed { index, rule ->
                            message += "$index] $rule\n"
                        }
                        event.channel.sendMessage(message).queue()
                    }
                    "add" -> {
                        val index = args[2].text.toInt()
                        if(index > rm.getRules().size) {
                            result.error("Bad index!")
                            return
                        }
                        var rule = ""
                        for(i in 3 until args.size) {
                            if(i != 3)
                                rule += " "
                            rule += args[i].text
                        }
                        rm.addRule(index, rule)
                    }
                    "remove" -> rm.removeRule(args[2].text.toInt())
                    "send" -> {
                        ChannelUtils.getMessage(server, args[2].text, object: IFunction {
                            override fun run(vararg args: Any) {
                                if(args.isEmpty()) {
                                    result.error("Message not found!")
                                    return
                                }
                                val builder = EmbedBuilder()
                                builder.setDescription("Please read these rules thoroughly and accept them at the bottom")
                                builder.setColor(Color(16544292))
                                builder.setTimestamp(Instant.now())
                                builder.setFooter("Last updated")
                                builder.setAuthor("Rules")
                                rm.getRules().forEachIndexed { index, rule -> builder.addField("Rule ${index + 1}", rule, false) }
                                builder.addField(":warning:", "By reacting with the emoji you agree to follow the rules.\nBreaking the might result in a mute/ban.", false)
                                val msg = args[0] as Message
                                msg.editMessage(MessageBuilder(builder).build()).queue()
                            }
                        })
                    }
                    else -> {
                        result.error("Bad argument! Use list/add/remove or send!")
                        return
                    }
                }
            }
            "rolemsg" -> {
                //TODO: Implement
            }
            "save" -> cfg.save()
            else -> {
                result.error("Bad command! try ${data.cmdPrefix}config help")
                return
            }
        }
        result.success()
    }
}