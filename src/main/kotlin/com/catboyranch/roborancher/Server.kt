package com.catboyranch.roborancher

import com.catboyranch.roborancher.configs.ServerConfig
import com.catboyranch.roborancher.managers.RuleManager
import net.dv8tion.jda.api.entities.Guild

class Server(val guild: Guild) {
    private val config: ServerConfig = ServerConfig(this)
    private val ruleManager = RuleManager(config.getData())

    init {
    }
}