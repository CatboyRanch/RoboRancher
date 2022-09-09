package com.catboyranch.roborancher

import com.catboyranch.roborancher.managers.SaveableManager
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class ServerConfig(@JsonIgnore private val server: Server) {
    var cmdPrefix = "!"
    var cagedRoleID = "setme"
    var adminRole = "setme"
    var memberRole = "setme"
    var deleteFilter = false
    var softFilter = arrayListOf("word1", "word2", "word3")
    var hardFilter = arrayListOf("word1", "word2", "word3")
    var praiseCooldown = 86400L
    val managers = ArrayList<SaveableManager>()

    @JsonIgnore private val configPath: String

    init {
        val jarPath = ""
        val serverID = ""
        configPath = "$jarPath/RoboRancher/servers/$serverID.json"
        if(File(configPath).exists())
            load()
        else
            save()
    }

    private fun load() {
        val o = ObjectMapper().readValue(File(configPath), ServerConfig::class.java)
        cagedRoleID = o.cagedRoleID
        adminRole = o.adminRole
        memberRole = o.memberRole
        deleteFilter = o.deleteFilter
        softFilter = o.softFilter
        hardFilter = o.hardFilter
        praiseCooldown = o.praiseCooldown
        managers.addAll(o.managers)
    }

    private fun save() = ObjectMapper().writeValue(File(configPath), this)
}