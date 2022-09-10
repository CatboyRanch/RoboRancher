package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.MarkdownSanitizer

class CPet(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "pet"
    override val description = "Pet your fellow catboys!"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN, RoleUtils.RoleType.MODERATOR, RoleUtils.RoleType.MEMBER)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        if(args.size == 1 && args[0].type == CommandArgument.TYPE.USER_TAG) {
            val petter = event.member
            val toPet = args[0].getMember()

            if(petter == null || toPet == null) {
                result.error("User not found!")
                return
            }

            val msg = getPetMessage(petter.effectiveName, toPet.effectiveName)
            event.channel.sendMessage(MarkdownSanitizer.escape(MarkdownSanitizer.sanitize(msg))).queue()
        } else {
            event.message.reply("@ someone to pet them!").queue()
        }
        result.success()
    }

    private fun getPetMessage(petter: String, toPet: String): String {
        val list = arrayOf("$petter gently pets $toPet",
            "$petter moves their hand over the soft hair of $toPet",
            "$petter pets $toPet",
            "$petter grabs $toPet by the hair, which could be seen as petting in some way i suppose",
            "$petter tries petting $toPet, but $toPet pets $petter back!",
            "$petter slaps $toPet on the ass and that's just a rough pet",
            "$petter is getting too rough with with $toPet! Somebody save them! …oh wait- Nevermind they’re just petting, sorry I forgot my glasses",
            "$petter is now petting $toPet. Now %toPet%'s hair is nice and floofy!",
            "$petter is about to pet $toPet but quickly moves there hands and tickles them! how naughty uwu")
        return list[Utils.getRandom(0, list.size)]
    }
}