package com.catboyranch.roborancher

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.Role
import java.util.regex.Pattern

class CommandArgument(private val server: Server, private val input: String) {
    enum class TYPE { TEXT, USER_TAG, CHANNEL_TAG, ROLE_TAG }
    val type: TYPE
    val text: String

    init {
        if(input.startsWith("<@&")) {
            type = TYPE.ROLE_TAG;
            text = get("<@&", ">", input);
        } else if(input.startsWith("<@")) {
            type = TYPE.USER_TAG;
            text = get("<@", ">", input);
        } else if(input.startsWith("<#")) {
            type = TYPE.CHANNEL_TAG;
            text = get("<#", ">", input);
        } else {
            type = TYPE.TEXT;
            text = input;
        }
    }

    fun getRole(): Role? = if(type == TYPE.ROLE_TAG) RoleUtils.getRole(server, text) else null
    fun getMember(): Member? = if(type == TYPE.USER_TAG) MemberUtils.getMember(server, text) else null
    fun getChannel(): MessageChannel? = if(type == TYPE.CHANNEL_TAG) ChannelUtils.getChannel(server, text) else null

    fun asMention(): String {
        return when(type) {
            TYPE.ROLE_TAG -> "<@&$text>"
            TYPE.USER_TAG -> "<@$text>"
            TYPE.CHANNEL_TAG -> "<#$text>"
            else -> text
        }
    }

    private fun get(start: String, end: String, input: String): String {
        val pattern = Pattern.compile("(?<=\\$start)(.*?)(?=\\$end)")
        val matcher = pattern.matcher(input)
        return if(matcher.find()) matcher.group() else "null"
    }
}