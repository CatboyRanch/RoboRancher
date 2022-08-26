package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.utils.*;
import com.catboyranch.roborancher.configs.RoleType;
import com.catboyranch.roborancher.configs.ServerConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;

import java.awt.*;
import java.time.Instant;

public class CConfig extends CommandBase{
    public CConfig(RoboRancher rancher) {
        super(rancher);
        name = "config";
        description = "Change config settings";
        allowedRoles = Utils.asArray(RoleType.ADMIN);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        ServerConfig cfg = server.getConfig();
        if(args.length == 0 || args[0].getText().equals("help")) {
            String cP = cfg.getCmdPrefix();
            String message =    ":desktop: General commands: :desktop:\n";
            message +=          String.format("%sconfig help\n", cP);
            message +=          String.format("%sconfig save\n", cP);
            message +=          String.format("%sconfig prefix <prefix> (current: %s)\n", cP, cfg.getCmdPrefix());
            message +=          String.format("%sconfig setrole <admin/mod/member/caged> <role-id> (current: admin(%s), moderator(%s), member(%s), caged(%s))\n", cP, RoleUtils.getRoleName(cfg.getAdminRole(), server), RoleUtils.getRoleName(cfg.getModRole(), server), RoleUtils.getRoleName(cfg.getMemberRole(), server), RoleUtils.getRoleName(cfg.getCagedRoleID(), server));
            message +=          "\n:desktop: Filter commands: :desktop:\n";
            message +=          String.format("%sconfig filter <true/false> (current: %s)\n", cP, cfg.isDeleteFilter());
            message +=          String.format("%sconfig addFilter <soft/hard> <word> (current: soft(|| %s ||), hard(|| %s ||)\n", cP, cfg.getSoftFilter(), cfg.getHardFilter());
            message +=          String.format("%sconfig delFilter <soft/hard> <word>\n", cP);
            message +=          "\n:desktop: Rules commands: :desktop:\n";
            message +=          String.format("%sconfig rules list\n", cP);
            message +=          String.format("%sconfig rules <add/remove> <index> <text>\n", cP);
            message +=          String.format("%sconfig rules send <id>\n", cP);
            message +=          "\n:desktop: Role message commands: :desktop:\n";
            message +=          String.format("%sconfig rolemsg add <message-id> <emoji> <role>\n", cP);
            message +=          String.format("%sconfig rolemsg remove <message-id> <emoji>", cP);

            event.getChannel().sendMessage(message).queue();
            result.success();
            return;
        }

        switch(args[0].getText()) {
            case "prefix" -> cfg.setCmdPrefix(args[1].getText());
            case "setrole" -> {
                String roleType = args[1].getText();
                String roleID = args[2].getText();
                if(!RoleUtils.isRole(server, roleID)) {
                    result.error("Role not found!");
                    return;
                }

                switch(roleType) {
                    case "admin" -> cfg.setAdminRole(roleID);
                    case "mod", "moderator" -> cfg.setModRole(roleID);
                    case "member" -> cfg.setMemberRole(roleID);
                    case "caged" -> cfg.setCagedRoleID(roleID);
                    default -> {
                        result.error("Bad role type!");
                        return;
                    }
                }
                result.success();
                return;
            }
            case "filter" -> {
                cfg.setDeleteFilter(Boolean.parseBoolean(args[1].getText()));
                result.success();
                return;
            }
            case "addFilter" -> {
                switch(args[1].getText()) {
                    case "soft" -> cfg.addSoftWord(args[2].getText());
                    case "hard" -> cfg.addHardWord(args[2].getText());
                    default -> {
                        result.error("Bad argument! Use soft or hard!");
                        return;
                    }
                }
                result.success();
                return;
            }
            case "delFilter" -> {
                switch(args[1].getText()) {
                    case "soft" -> cfg.removeSoftWord(args[2].getText());
                    case "hard" -> cfg.removeHardWord(args[2].getText());
                    default -> {
                        result.error("Bad argument! Use soft or hard!");
                        return;
                    }
                }
                result.success();
                return;
            }
            case "rules" -> {
                switch (args[1].getText()) {
                    case "list" -> {
                        StringBuilder message = new StringBuilder("Rules: (Remember, arrays start at 0! :nerd_face:)\n");
                        int index = 0;
                        for (String rule : cfg.getRuleFile().getRules()) {
                            message.append(String.format("%s] %s\n", index, rule));
                            index++;
                        }
                        event.getChannel().sendMessage(message.toString()).queue();
                        result.success();
                        return;
                    }
                    case "add" -> {
                        StringBuilder rule = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            if (i != 3)
                                rule.append(" ");
                            rule.append(args[i].getText());
                        }
                        if (!cfg.getRuleFile().addRule(Integer.parseInt(args[2].getText()), rule.toString())) {
                            result.error("Could not add rule! Did you supply a good index?");
                            return;
                        }
                        result.success();
                        return;
                    }
                    case "remove" -> {
                        cfg.getRuleFile().removeRule(Integer.parseInt(args[2].getText()));
                        result.success();
                        return;
                    }
                    case "send" -> {
                        ChannelUtils.getMessage(server, args[2].getText(), objects -> {
                            if (objects.length == 0) {
                                result.error("Message not found!");
                                return;
                            }
                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setDescription("Please read these rules thoroughly and accept them at the bottom");
                            builder.setColor(new Color(16544292));
                            builder.setTimestamp(Instant.now());
                            builder.setFooter("Last updated");
                            builder.setAuthor("Rules");

                            int index = 1;
                            for (String rule : cfg.getRuleFile().getRules()) {
                                builder.addField(String.format("Rule %s", index), rule, false);
                                index++;
                            }
                            builder.addField(":warning:", "By reacting with the :warning: you agree to follow the rules.\nBreaking the might result in a mute/ban.", false);
                            ((Message) objects[0]).editMessage(new MessageBuilder(builder).build()).queue();
                        });
                        return;
                    }
                    default -> {
                        result.error("Bad argument! Use list/add/remove or send!");
                        return;
                    }
                }
            }
            case "rolemsg" -> {
                String type = args[1].getText();
                String messageID = args[2].getText();

                ChannelUtils.getMessage(server, messageID, objects -> {
                    if(objects.length == 0) {
                        result.error("Message not found!");
                        return;
                    }

                    Message message = (Message)objects[0];

                    CommandArgument emojiInput = args[3];
                    if(!emojiInput.isAnyEmojiKind()) {
                        result.error(String.format("%s is not a valid emoji!", emojiInput.getText()));
                        return;
                    }

                    switch(type) {
                        case "add" -> {
                            CommandArgument roleInput = args[4];
                            Role role = roleInput.getType() == CommandArgument.TYPE.ROLE_TAG ? roleInput.getRole() : RoleUtils.getRole(roleInput.getText(), server);
                            if(role == null) {
                                result.error(String.format("%s is not a valid role!", roleInput.getText()));
                                return;
                            }
                            cfg.addRoleMessageEmoji(message, Emoji.fromFormatted(emojiInput.getText()), role);
                            cfg.ensureRoleMessageEmojis();
                        }
                        case "remove" -> cfg.removeRoleMessageEmoji(message, Emoji.fromFormatted(emojiInput.getText()));
                    }
                    result.success();
                });
                return;
            }
            case "save" -> cfg.save();
            default -> {
                result.error(String.format("Bad command! Try %sconfig help", cfg.getCmdPrefix()));
                return;
            }
        }

        result.success();
    }
}
