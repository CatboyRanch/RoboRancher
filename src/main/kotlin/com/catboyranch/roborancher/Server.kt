package com.catboyranch.roborancher

import com.catboyranch.roborancher.managers.RuleManager
import net.dv8tion.jda.api.entities.Guild

class Server(val guild: Guild) {
    private val config: ServerConfig = ServerConfig(this)
    private lateinit var ruleManager: RuleManager

    init {
        config.managers.forEach {
            when(it) {
                is RuleManager -> ruleManager = it
            }
        }
    }
}