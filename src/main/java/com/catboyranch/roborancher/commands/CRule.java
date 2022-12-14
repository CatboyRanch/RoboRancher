package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.managers.RuleManager;
import com.catboyranch.roborancher.utils.RoleType;
import com.catboyranch.roborancher.utils.CommandArgument;
import com.catboyranch.roborancher.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CRule extends CommandBase{

    public CRule(RoboRancher rancher) {
        super(rancher);
        name = "rule";
        description = "Show a specific rule";
        allowedRoles = Utils.asArray(RoleType.ADMIN, RoleType.MODERATOR, RoleType.MEMBER);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        if(args.length == 0) {
            result.error("Please add an index for whichever rule you want to quote!");
            return;
        }

        if(!Utils.isInteger(args[0].getText())) {
            result.error("Argument must be a number!");
            return;
        }

        int index = Integer.parseInt(args[0].getText());
        RuleManager rm = server.getRuleManager();
        if(index <= 0 || index > rm.getRules().size()) {
            result.error("Bad index!");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(String.format("Rule %s", index));
        eb.setFooter(String.format("Requested by %s", event.getMember().getEffectiveName()));
        index--;
        eb.setDescription(rm.getRules().get(index));
        event.getMessage().delete().queue();
        event.getChannel().sendMessage(new MessageBuilder(eb).build()).queue();
        result.successQuiet();
    }
}
