package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.configs.RoleType;
import com.catboyranch.roborancher.utils.CommandArgument;
import com.catboyranch.roborancher.utils.MemberUtils;
import com.catboyranch.roborancher.utils.TimestampType;
import com.catboyranch.roborancher.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDateTime;

public class CInfo extends CommandBase{

    public CInfo(RoboRancher rancher) {
        super(rancher);
        name = "info";
        description = "Get info for user";
        allowedRoles = Utils.asArray(RoleType.ADMIN, RoleType.MODERATOR);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        if(args.length == 0) {
            event.getMessage().reply("Tag someone or supply a user id!").queue();
            result.success();
            return;
        }

        Member member = args[0].getType() == CommandArgument.TYPE.USER_TAG ? args[0].getMember() : MemberUtils.getMemberById(server, args[0].getText());
        String message = "User info:\n";
        message +=      String.format("ID: %s\n", member.getId());
        message +=      String.format("Current username: %s#%s (%s)\n", member.getUser().getName(), member.getUser().getDiscriminator(), TimestampType.LONG_DATE_SHORT_TIME.formatNow());
        message +=      String.format("Mention: %s\n", member.getAsMention());
        message +=      String.format("Account created: %s\n", TimestampType.LONG_DATE_SHORT_TIME.format(member.getTimeCreated()));
        message +=      String.format("Time joined: %s\n", TimestampType.LONG_DATE_SHORT_TIME.format(member.getTimeJoined()));
        message +=      String.format("Time boosted: %s\n", TimestampType.LONG_DATE_SHORT_TIME.format(member.getTimeBoosted()));
        event.getChannel().sendMessage(message).queue();
    }
}
