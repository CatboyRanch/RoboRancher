package com.catboyranch.roborancher.managers

import com.fasterxml.jackson.annotation.JsonAutoDetect

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class RuleManager: SaveableManager {
    private val rules = ArrayList<Rule>()

    fun addRule(index: Int, text: String) = rules.add(index, Rule(index, text))
    fun removeRule(index: Int) = rules.removeAt(index)
}

data class Rule(val index: Int, val content: String)