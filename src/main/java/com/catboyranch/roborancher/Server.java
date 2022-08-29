package com.catboyranch.roborancher;

import com.catboyranch.roborancher.managers.CageManager;
import com.catboyranch.roborancher.managers.RoleMessageManager;
import com.catboyranch.roborancher.managers.RuleManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.*;

public class Server {
    @Getter
    private final ServerConfig config;
    @Getter
    private final Guild guild;

    //Managers
    @Getter
    private final RoleMessageManager roleMessageManager = new RoleMessageManager(this);
    @Getter
    private final RuleManager ruleManager = new RuleManager();
    @Getter
    private final CageManager cageManager = new CageManager(this);

    public Server(Guild guild) {
        this.guild = guild;
        config = new ServerConfig(this);
        roleMessageManager.ensureRoleMessageEmojis();
    }
}
