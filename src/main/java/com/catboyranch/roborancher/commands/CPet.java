package com.catboyranch.roborancher.commands;

import com.catboyranch.roborancher.utils.RoleType;
import com.catboyranch.roborancher.utils.CommandArgument;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import com.catboyranch.roborancher.RoboRancher;
import com.catboyranch.roborancher.Server;
import com.catboyranch.roborancher.utils.Utils;

public class CPet extends CommandBase {
    private final String[] messages = {
        "%petter% gently pets %toPet%",
        "%petter% moves their hand over the soft hair of %toPet%",
        "%petter% pets %toPet%",
        "%petter% grabs %toPet% by the hair, which could be seen as petting in some way i suppose",
        "%petter% tries petting %toPet%, but %toPet% pets %petter% back!",
        "%petter% slaps %toPet% on the ass and that's just a rough pet",
        "%petter% is getting too rough with with %toPet%! Somebody save them! …oh wait- Nevermind they’re just petting, sorry I forgot my glasses",
        "%petter% is now petting %toPet%. Now %toPet%'s hair is nice and floofy!",
        "%petter% is about to pet %topet% but quickly moves there hands and tickles them! how naughty uwu"
    };

    private final String selfPet = "%petter% is a strong independent catboy who can pet themselves";

    public CPet(RoboRancher rancher) {
        super(rancher);
        name = "pet";
        description = "Pet your fellow catboys!";
        allowedRoles = Utils.asArray(RoleType.ADMIN, RoleType.MODERATOR, RoleType.MEMBER);
    }

    @Override
    public void run(String cmd, Server server, MessageReceivedEvent event, CommandResult result, CommandArgument... args) {
        if(args.length == 1 && args[0].getType() == CommandArgument.TYPE.USER_TAG) {
            Member petter = event.getMember();
            Member toPet = args[0].getMember();

            if(petter == null || toPet == null) {
                result.error("User not found!");
                return;
            }

            String message = petter.getId().equals(toPet.getId()) ? selfPet : messages[Utils.getRandom(0, messages.length-1)];
            message = message.replaceAll("%petter%", petter.getEffectiveName());
            message = message.replaceAll("%toPet%", toPet.getEffectiveName());
            message = MarkdownSanitizer.sanitize(message);
            message = MarkdownSanitizer.escape(message);
            event.getChannel().sendMessage(message).queue();
        } else {
            event.getMessage().reply("@ someone to pet them!").queue();
        }
        result.success();
    }

}
