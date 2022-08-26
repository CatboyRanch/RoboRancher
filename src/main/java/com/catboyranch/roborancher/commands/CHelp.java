package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.configs.RoleType;
import com.catboyranch.roborancher.utils.CommandArgument;
import com.catboyranch.roborancher.utils.RoleUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.utils.Utils;

import java.util.Map;

public class CHelp extends CommandBase{

    public CHelp(RoboRancher rancher) {
        super(rancher);
        name = "help";
        description = "View all commands!";
        allowedRoles = Utils.asArray(RoleType.ADMIN, RoleType.MODERATOR, RoleType.MEMBER);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        Member member = event.getMember();
        if(member == null) {
            result.error("This should not happen. Member not found?");
            return;
        }

        StringBuilder message = new StringBuilder("All commands:\n");
        for (Map.Entry<String, CommandBase> commandMap : rancher.getCmdManager().commands.entrySet()) {
            boolean show = false;

            CommandBase command = commandMap.getValue();
            for (RoleType role : command.getAllowedRoles()) {
                if(RoleUtils.hasRole(server, member, role) || RoleUtils.hasAdminPermission(member))
                    show = true;
            }
            if (show)
                message.append(command.getName()).append(": ").append(command.getDescription()).append("\n");
        }
        event.getChannel().sendMessage(message.toString()).queue();
        result.success();
    }


}
