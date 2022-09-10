package com.catboyranch.roborancher.managers

import com.catboyranch.roborancher.configs.ServerConfigData
import net.dv8tion.jda.api.entities.Member

class CageManager(private val data: ServerConfigData) {
    //TODO: Implement

    fun cageIfNeeded(member: Member) {

    }

    fun isCaged(member: Member): Boolean {
        return false
    }
}