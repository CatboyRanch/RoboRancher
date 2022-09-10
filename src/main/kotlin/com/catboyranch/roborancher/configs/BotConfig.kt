package com.catboyranch.roborancher.configs

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class BotConfigData {
    var token = "token"
    var activity = "Volleyball"
}

class BotConfig {
    private val location = File("RoboRancher/cfg/config.json")
    private lateinit var data: BotConfigData

    fun init() {
        when(location.exists()) {
            true -> load()
            false -> {
                data = BotConfigData()
                save()
            }
        }
    }

    fun setToken(token: String) { data.token = token }
    fun getToken(): String = data.token

    fun setActivity(activity: String) { data.activity = activity }
    fun getActivity(): String = data.activity

    private fun load() {
        data = ObjectMapper().readValue(location, BotConfigData::class.java)
    }

    fun save() = ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(location, data)
}