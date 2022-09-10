package com.catboyranch.roborancher.managers

import com.catboyranch.roborancher.configs.ServerConfigData

class RuleManager(private val configData: ServerConfigData) {
    fun addRule(index: Int, text: String) = configData.rules.add(index, text)
    fun removeRule(index: Int) = configData.rules.removeAt(index)
    fun getRules(): ArrayList<String> = configData.rules
}
