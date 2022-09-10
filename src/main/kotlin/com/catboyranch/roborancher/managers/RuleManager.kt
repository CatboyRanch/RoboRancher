package com.catboyranch.roborancher.managers

import com.catboyranch.roborancher.configs.ServerConfigData

class RuleManager(private val configData: ServerConfigData) {
    fun addRule(index: Int, text: String) = configData.rules.add(index, text)
    fun removeRule(index: Int) = configData.rules.removeAt(index)

    override fun toString(): String = configData.rules.toString()
}
