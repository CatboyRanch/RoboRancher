package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.managers.PraiseManager;
import com.catboyranch.roborancher.utils.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.collections4.comparators.ComparableComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class CPraise extends CommandBase{

    public CPraise(RoboRancher rancher) {
        super(rancher);
        name = "praise";
        description = "Praise command";
        allowedRoles = Utils.asArray(RoleType.ADMIN, RoleType.MODERATOR, RoleType.MEMBER);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        PraiseManager pm = server.getPraiseManager();
        Member praiser = event.getMember();
        if(praiser == null) {
            result.error("Member is null");
            return;
        }

        if(args.length == 0 || args[0].getText().equals("help")) {
            String prefix = server.getConfig().getCmdPrefix();
            String help = "Commands:\n";
            help +=     String.format("%spraise <user>\n", prefix);
            help +=     String.format("%spraise list\n", prefix);
            help +=     String.format("%spraise cooldown", prefix);
            event.getMessage().reply(help).queue();
            result.success();
            return;
        }

        if(args[0].getType() == CommandArgument.TYPE.USER_TAG) {
            Member toPraise = args[0].getMember();
            if(pm.isOnCooldown(praiser)) {
                String timestamp = TimestampType.RELATIVE.format(pm.getCooldown(praiser));
                event.getMessage().reply(String.format("You are currently on cooldown! See you in %s!", timestamp)).queue();
                result.success();
                return;
            }
            pm.addCooldown(praiser);
            pm.addPraise(toPraise);
            event.getMessage().reply(String.format("You praised %s! They have now been praised %s times!", toPraise.getEffectiveName(), pm.getPraises(toPraise))).queue();
            result.success();
            return;
        } else if(args[0].getType() == CommandArgument.TYPE.TEXT && args[0].getText().equals("list")) {
            ArrayList<KeyValueStorage<Integer, String>> users = new ArrayList<>();
            for(String userID : pm.getPraises().keySet()) {
                int praises = pm.getPraises().get(userID);
                users.add(new KeyValueStorage<>(praises, userID));
            }
            users.sort((o1, o2) -> o2.getKey() - o1.getKey());

            StringBuilder message = new StringBuilder("==Praise leaderboard==\n");
            for(KeyValueStorage<Integer, String> kvs : users) {
                Member member = MemberUtils.getMemberById(server, kvs.getValue());
                message.append(String.format("%s Praises: %s\n", kvs.getKey(), member.getEffectiveName()));
            }
            event.getChannel().sendMessage(message.toString()).queue();
            result.success();
            return;
        } else if(args[0].getType() == CommandArgument.TYPE.TEXT && args[0].getText().equals("cooldown")) {
            if(pm.isOnCooldown(praiser)) {
                String timestamp = TimestampType.RELATIVE.format(pm.getCooldown(praiser));
                event.getMessage().reply(String.format("You are on cooldown till %s!", timestamp)).queue();
                result.success();
                return;
            }
            event.getMessage().reply("You are not on cooldown!").queue();
            result.success();
            return;
        }
        result.error("Bad command! Try !praise help");
    }
}
