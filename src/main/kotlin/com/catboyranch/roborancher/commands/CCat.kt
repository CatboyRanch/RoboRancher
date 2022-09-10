package com.catboyranch.roborancher.commands

import com.catboyranch.roborancher.RoboRancher
import com.catboyranch.roborancher.RoleUtils
import com.catboyranch.roborancher.Server
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

class CCat(rancher: RoboRancher): CommandBase(rancher) {
    override val name = "cat"
    override val description = "Cat"
    override val allowedRoles = arrayOf(RoleUtils.RoleType.ADMIN, RoleUtils.RoleType.MODERATOR, RoleUtils.RoleType.MEMBER)

    override fun run(cmd: String, server: Server, event: MessageReceivedEvent, result: CommandResult, vararg args: CommandArgument) {
        try {
            val file = File.createTempFile("cat", ".png")
            ImageIO.write(ImageIO.read(URL("https://thiscatdoesnotexist.com")), "png", file)
            event.channel.sendFile(file).queue()
        } catch (exception: Throwable) {
            result.error("Error fetching image! Reason: ${exception.message}")
            return
        }
        result.success()
    }
}