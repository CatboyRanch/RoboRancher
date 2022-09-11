package com.catboyranch.roborancher

import com.catboyranch.roborancher.configs.ServerConfig
import com.catboyranch.roborancher.managers.CageManager
import com.catboyranch.roborancher.managers.RoleMessageManager
import com.catboyranch.roborancher.managers.RuleManager
import net.dv8tion.jda.api.entities.Guild

class Server(val guild: Guild) {
    val config: ServerConfig = ServerConfig(this)
    val ruleManager = RuleManager(config.getData())
    val cageManager = CageManager(this)
    val roleMessageManager = RoleMessageManager(this)

    init {
        roleMessageManager.ensureRoleMessageEmojis()
    }
}