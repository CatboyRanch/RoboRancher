package com.catboyranch.roborancher

import com.catboyranch.roborancher.managers.RuleManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.io.File
import javax.security.auth.login.LoginException

class RoboRancher {
    private lateinit var jda: JDA
    private val servers = ArrayList<Server>()

    init {
        setupJDA()
        jda.guilds.forEach { servers.add(Server(it)) }
    }

    private fun setupJDA() {
        File("RoboRancher/cfg/").mkdirs()
        File("RoboRancher/servers/").mkdirs()
        val botConfig = BotConfig()
        botConfig.init()
        while(botConfig.token == "token" || botConfig.token.isBlank()) {
            print("Please enter token: ")
            val token = readLine()
            if(token != null) {
                botConfig.token = token
                botConfig.save()
            }
        }

        val builder = JDABuilder.create(botConfig.token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES)
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
        builder.setBulkDeleteSplittingEnabled(false)
        builder.setActivity(Activity.playing(botConfig.activity))
        //Add listeners
        try {
            jda = builder.build()
            jda.awaitReady()
        } catch (exception: LoginException) {
            botConfig.token = "token"
            botConfig.save()
            println("Bad token!\n")
            setupJDA()
        }
    }
}