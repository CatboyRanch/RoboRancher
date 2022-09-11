package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.*
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

private fun cageCommandsBase(result: CommandResult, vararg args: CommandArgument, server: Server): Member? {
    if(args.isEmpty()) {
        result.error("Please tag a user or enter an id!")
        return null
    }

    val toCage = if(args[0].type == CommandArgument.TYPE.USER_TAG) args[0].getMember() else MemberUtils.getMember(server, args[0].text)
    if(toCage == null) {
        result.error("Member not found!")
        return null
    }
    return toCage
}

class CCage(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "cage"
    override val description = "Cage user"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN, RoleUtils.RoleType.MODERATOR)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        val toCage = cageCommandsBase(result, args = args, server = server) ?: return
        val cm = server.cageManager
        if(cm.isCaged(toCage)) {
            result.error("Member is already caged!")
            return
        }

        cm.cage(toCage)
        result.success()
    }
}

class CUncage(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "uncage"
    override val description = "Uncage user"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN, RoleUtils.RoleType.MODERATOR)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        val toCage = cageCommandsBase(result, args = args, server = server) ?: return
        val cm = server.cageManager
        if(!cm.isCaged(toCage)) {
            result.error("Member is not caged!")
            return
        }

        cm.uncage(toCage)
        result.success()
    }
}