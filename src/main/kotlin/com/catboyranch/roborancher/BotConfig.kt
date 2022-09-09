package com.catboyranch.roborancher

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class BotConfig {
    @JsonIgnore private val location = File("RoboRancher/cfg/config.json")
    var token: String = "token"
    var activity: String = "Volleyball"

    init {
        if(location.exists())
            load()
        else
            save()
    }

    private fun load() {
        val o = ObjectMapper().readValue(location, BotConfig::class.java)
        token = o.token
        activity = o.activity
    }

    fun save() {
        if(!location.exists())
            location.createNewFile()
        ObjectMapper().writeValue(location, this)
    }
}