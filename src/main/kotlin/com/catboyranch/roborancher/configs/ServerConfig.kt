package com.catboyranch.roborancher.configs

import com.catboyranch.roborancher.FileUtils
import com.catboyranch.roborancher.Server
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import javax.xml.crypto.Data

class ServerConfigData {
    var cmdPrefix = "!"
    var cagedRoleID = "setme"
    var adminRole = "setme"
    var memberRole = "setme"
    var deleteFilter = false
    var softFilter = arrayListOf("word1", "word2", "word3")
    var hardFilter = arrayListOf("word1", "word2", "word3")
    var praiseCooldown = 86400L
    var rules = ArrayList<String>()
}

class ServerConfig(@JsonIgnore private val server: Server) {
    private val configPath: String = "${FileUtils.getJarDirectory()}/RoboRancher/servers/${server.guild.id}.json"
    private lateinit var data: ServerConfigData

    init {
        when(File(configPath).exists()) {
            true -> load()
            false -> {
                data = ServerConfigData()
                save()
            }
        }
    }

    fun getData(): ServerConfigData = data

    private fun load() {
        data = ObjectMapper().readValue(File(configPath), ServerConfigData::class.java)
    }

    fun save() = ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(File(configPath), data)
}