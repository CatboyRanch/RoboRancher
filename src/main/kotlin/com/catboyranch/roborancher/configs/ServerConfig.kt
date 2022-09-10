package com.catboyranch.roborancher.configs

import com.catboyranch.roborancher.FileUtils
import com.catboyranch.roborancher.Server
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ServerConfigData {
    var cmdPrefix = "!"
    var cagedRoleID = "setme"
    var adminRole = "setme"
    var modRole = "setme"
    var memberRole = "setme"
    var deleteFilter = false
    var softFilter = arrayListOf("word1", "word2", "word3")
    var hardFilter = arrayListOf("word1", "word2", "word3")
    var praiseCooldown = 86400L
    var rules = ArrayList<String>()
    var cagedUsers = HashMap<String, ArrayList<String>>()
}

class ServerConfig(private val server: Server) {
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

    fun isFilterHard(string: String): Boolean {
        data.hardFilter.forEach { if(string.lowercase() == it) return true }
        return false
    }

    fun isFilterSoft(string: String): Boolean {
        data.softFilter.forEach {
            val wordCutoff = 4
            if (string.contains(" $it ") || string.contains(" $it") || string.contains("$it ") || string == it || (string.contains(it) && string.length <= it.length + wordCutoff))
                return true
        }
        return false
    }

    private fun load() { data = ObjectMapper().readValue(File(configPath), ServerConfigData::class.java) }
    fun save() = ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(File(configPath), data)
}