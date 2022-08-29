package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.utils.RoleType;
import com.catboyranch.roborancher.utils.CommandArgument;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.utils.Utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;

public class CCat extends CommandBase{
    private final static String IMAGE_URL = "https://thiscatdoesnotexist.com";

    public CCat(RoboRancher rancher) {
        super(rancher);
        name = "cat";
        description = "Cat";
        allowedRoles = Utils.asArray(RoleType.ADMIN, RoleType.MODERATOR, RoleType.MEMBER);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        try {
            File file = File.createTempFile("cat", ".png");
            ImageIO.write(ImageIO.read(new URL(IMAGE_URL)), "png", file);
            event.getChannel().sendFile(file).queue();
        } catch (Exception e) {
            result.error(String.format("Error fetching image. (%s)", e.getMessage()));
            return;
        }
        result.success();
    }

}
