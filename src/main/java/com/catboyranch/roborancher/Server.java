package com.catboyranch.roborancher;

import com.catboyranch.roborancher.configs.ServerConfig;
import lombok.Getter;
import net.dv8tion.jda.api.entities.*;

public class Server {
    @Getter
    private final ServerConfig config;
    @Getter
    private final Guild guild;

    public Server (Guild guild) {
        this.guild = guild;
        config = new ServerConfig(this);
        config.ensureRoleMessageEmojis();
    }
}
