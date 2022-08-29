package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.utils.CommandArgument;
import com.catboyranch.roborancher.utils.Utils;
import com.catboyranch.roborancher.utils.RoleType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;

public class CSay extends CommandBase{

    public CSay(RoboRancher rancher) {
        super(rancher);
        name = "say";
        description = "Speak catboy!";
        allowedRoles = Utils.asArray(RoleType.ADMIN);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        String message = event.getMessage().getContentRaw();
        message = message.replace(server.getConfig().getCmdPrefix() + name, "");
        event.getMessage().delete().queue();
        event.getChannel().sendMessage(message).queue();
        result.successQuiet();
    }
}
