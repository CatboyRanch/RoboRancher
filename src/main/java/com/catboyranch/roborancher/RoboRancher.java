package com.catboyranch.roborancher;

import com.catboyranch.roborancher.commands.*;
import com.catboyranch.roborancher.events.JoinListener;
import com.catboyranch.roborancher.events.MessageEvents;
import com.catboyranch.roborancher.utils.FileUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class RoboRancher {
    private static JDA jda;
    private final CommandManager cmdManager;
    private final HashMap<String, Server> servers = new HashMap<>();

    public RoboRancher() {
        setupJDA();
        loadServers();

        cmdManager = new CommandManager(this);
        cmdManager.registerCommand(new CConfig(this));
        cmdManager.registerCommand(new CSay(this));
        cmdManager.registerCommand(new CPet(this));
        cmdManager.registerCommand(new CEdit(this));
        cmdManager.registerCommand(new CHelp(this));
        cmdManager.registerCommand(new CCage(this));
        cmdManager.registerCommand(new CUncage(this));
        cmdManager.registerCommand(new CCat(this));
        cmdManager.registerCommand(new CInfo(this));
    }

    private void loadServers() {
        for(Guild g : jda.getGuilds()) {
            servers.put(g.getId(), new Server(g));
        }
    }

    private void setupJDA(){
        JSONObject mainConfig = new JSONObject(FileUtils.loadAndVerify("RoboRancher/cfg/", "config.json"));
        if(mainConfig.getString("token").equals("token")) {
            System.out.println("Please set your token in the config.json file.");
            System.exit(0);
        }

        JDABuilder builder = JDABuilder.create(mainConfig.getString("token"), GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES);
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setActivity(Activity.playing(mainConfig.getString("activityGame")));
        builder.addEventListeners(new JoinListener(this), new MessageEvents(this));
        try {
            jda = builder.build();
            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public CommandManager getCmdManager() {
        return cmdManager;
    }

    public static JDA getJDA() {
        return jda;
    }

    public Server getServer(String id) {
        return servers.get(id);
    }

    public static boolean isOurBot(String otherID) {
        return jda.getSelfUser().getId().equals(otherID);
    }
}