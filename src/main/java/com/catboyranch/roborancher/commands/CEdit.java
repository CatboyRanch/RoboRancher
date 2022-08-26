package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.utils.ChannelUtils;
import com.catboyranch.roborancher.utils.CommandArgument;
import com.catboyranch.roborancher.utils.Utils;
import com.catboyranch.roborancher.configs.RoleType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;

public class CEdit extends CommandBase {

    public CEdit(RoboRancher rancher) {
        super(rancher);
        name = "edit";
        description = "Edit messages sent by me!";
        allowedRoles = Utils.asArray(RoleType.ADMIN);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        if(args.length < 2) {
            result.error("Not enough arguments!");
            return;
        }

        String messageID = args[0].getText();
        String format = String.format("%s%s %s", server.getConfig().getCmdPrefix(), cmd, messageID);
        final String newMessage = event.getMessage().getContentRaw().replace(format, "");

        ChannelUtils.getMessage(server, messageID, objects -> {
            if(objects.length == 0) {
                result.error("Could not find message!");
                return;
            }
            ((Message)objects[0]).editMessage(newMessage).queue();
            result.success();
        });
    }

}
