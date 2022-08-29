package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.utils.RoleType;
import com.catboyranch.roborancher.ServerConfig;
import com.catboyranch.roborancher.utils.ChannelUtils;
import com.catboyranch.roborancher.utils.CommandArgument;
import com.catboyranch.roborancher.utils.RoleUtils;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CommandManager extends ListenerAdapter {
    private final RoboRancher rancher;
    public final HashMap<String, CommandBase> commands = new HashMap<>();

    public CommandManager(RoboRancher rancher) {
        this.rancher = rancher;
        rancher.getJDA().addEventListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //Don't reply to dm's or other bots
        if(event.getChannelType() == ChannelType.PRIVATE || event.getAuthor().isBot())
            return;

        Member member = event.getMember();
        if(member == null)
            return;
        Server server = rancher.getServer(event.getGuild().getId());
        ServerConfig cfg = server.getConfig();
        String msgContent = event.getMessage().getContentRaw();

        //Run filters
        if(cfg.isDeleteFilter() && !event.getGuildChannel().asTextChannel().isNSFW() && !RoleUtils.hasAdminPermission(member) && !RoleUtils.hasRole( member, cfg.getAdminRole())) {
            if(cfg.isFilterHard(msgContent) || cfg.isFilterSoft(msgContent))
                ChannelUtils.deleteMessage(event.getMessage());
        }

        String prefix = cfg.getCmdPrefix();
        if (msgContent.startsWith(prefix)) {
            msgContent = msgContent.replace(prefix, "");
            String[] argsRaw = msgContent.split(" ");

            String cmd = argsRaw[0];
            if (!commands.containsKey(cmd))
                return;

            argsRaw = Arrays.copyOfRange(argsRaw, 1, argsRaw.length);

            ArrayList<CommandArgument> argsList = new ArrayList<>();
            for(String arg : argsRaw) {
                if(!arg.equals(" ")) {
                    argsList.add(new CommandArgument(server, arg));
                }
            }
            CommandArgument[] args = new CommandArgument[argsList.size()];
            for(int i = 0; i < argsList.size(); i++) {
                args[i] = argsList.get(i);
            }

            boolean hasAdminPerm = RoleUtils.hasAdminPermission(member);
            CommandBase c = commands.get(cmd);
            boolean runCommand = false;

            if(!hasAdminPerm) {
                for (RoleType role : c.getAllowedRoles()) {
                    if (RoleUtils.hasRole(server, member, role)) {
                        runCommand = true;
                        break;
                    }
                }
            }

            if (runCommand || hasAdminPerm) {
                CommandResult result = new CommandResult() {
                    final Message msg = event.getMessage();

                    private void r(String emoji) {
                        msg.addReaction(Emoji.fromUnicode(emoji)).queue();
                    }

                    @Override
                    public void success() {
                        r("✅");
                    }

                    @Override
                    public void error(String message) {
                        r("❌");
                        msg.reply(message).queue();
                    }

                    @Override
                    public void successQuiet() { }
                };
                c.run(cmd, server, event, result, args);
            }
        }
    }

    public void registerCommand(CommandBase cmd) {
        commands.put(cmd.getName(), cmd);
    }

}
