package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.utils.MemberUtils;
import com.catboyranch.roborancher.utils.CommandArgument;
import com.catboyranch.roborancher.utils.Utils;
import com.catboyranch.roborancher.configs.RoleType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;

public class CCage extends CommandBase{

    public CCage(RoboRancher rancher) {
        super(rancher);
        name = "cage";
        description = "Cage user";
        allowedRoles = Utils.asArray(RoleType.ADMIN, RoleType.MODERATOR);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        if(args.length == 0) {
            result.error("Please tag a user or enter an id!");
            return;
        }

        Member toCage = args[0].getType() == CommandArgument.TYPE.USER_TAG ? args[0].getMember() : MemberUtils.getMemberById(server, args[0].getText());
        if(toCage != null) {
            server.getConfig().cageUser(toCage);
            result.success();
            return;
        }
        result.error("User not found!");
    }

}