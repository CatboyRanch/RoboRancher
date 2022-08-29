package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.utils.RoleType;
import com.catboyranch.roborancher.utils.CommandArgument;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;

public class CommandBase {
    protected RoboRancher rancher;

    protected String name;
    protected String description;
    protected RoleType[] allowedRoles;

    public CommandBase(RoboRancher rancher) {
        this.rancher = rancher;
    }

    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {}

    public RoleType[] getAllowedRoles() {
        return allowedRoles;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
