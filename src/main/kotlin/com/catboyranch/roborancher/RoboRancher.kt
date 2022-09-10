package com.catboyranch.roborancher

import com.catboyranch.roborancher.configs.BotConfig
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.io.File
import javax.security.auth.login.LoginException

fun main() {
    RoboRancher()
}

class RoboRancher {
    private lateinit var jda: JDA
    private val servers = ArrayList<Server>()

    init {
        setupJDA()
        jda.guilds.forEach { servers.add(Server(it)) }
    }

    fun getServer(id: String):Server? {
        servers.forEach { if(it.guild.id == id) return it }
        return null
    }

    private fun setupJDA() {
        File("RoboRancher/cfg/").mkdirs()
        File("RoboRancher/servers/").mkdirs()
        val botConfig = BotConfig()
        botConfig.init()
        while(botConfig.getToken() == "token" || botConfig.getToken().isBlank()) {
            print("Please enter token: ")
            val token = readLine()
            if(token != null) {
                botConfig.setToken(token)
                botConfig.save()
            }
        }

        val builder = JDABuilder.create(botConfig.getToken(), GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES)
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
        builder.setBulkDeleteSplittingEnabled(false)
        builder.setActivity(Activity.playing(botConfig.getActivity()))
        //Add listeners
        try {
            jda = builder.build()
            jda.awaitReady()
        } catch (exception: LoginException) {
            botConfig.setToken("token")
            botConfig.save()
            println("Bad token!\n")
            setupJDA()
        }
    }
}