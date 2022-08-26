package com.catboyranch.roborancher.utils;

import com.catboyranch.roborancher.Server;
import lombok.Getter;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandArgument {
    public enum TYPE {TEXT, USER_TAG, CHANNEL_TAG, ROLE_TAG, UNICODE_EMOJI, DISCORD_EMOJI, DISCORD_EMOJI_ANIMATED}

    private final Server server;
    @Getter
    private final String text;
    @Getter
    private final TYPE type;

    public CommandArgument(Server server, String input) {
        this.server = server;
        if(input.startsWith("<@&")) {
            type = TYPE.ROLE_TAG;
            text = get("<@&", ">", input);
        } else if(input.startsWith("<@")) {
            type = TYPE.USER_TAG;
            text = get("<@", ">", input);
        } else if(input.startsWith("<#")) {
            type = TYPE.CHANNEL_TAG;
            text = get("<#", ">", input);
        } else if(input.startsWith("<:")) {
            type = TYPE.DISCORD_EMOJI;
            text = input;
        } else if(input.startsWith("<a:")) {
            type = TYPE.DISCORD_EMOJI_ANIMATED;
            text = input;
        } else if(Utils.isUnicodeEmoji(input)) {
            type = TYPE.UNICODE_EMOJI;
            text = input;
        } else {
            type = TYPE.TEXT;
            text = input;
        }
    }

    public Member getMember() {
        if(type != TYPE.USER_TAG)
            return null;
        return server.getGuild().getMemberById(text);
    }

    public GuildChannel getChannel() {
        if(type != TYPE.CHANNEL_TAG)
            return null;
        return server.getGuild().getGuildChannelById(text);
    }

    public Role getRole() {
        if(type != TYPE.ROLE_TAG)
            return null;
        return server.getGuild().getRoleById(text);
    }

    public String asMention() {
        switch(type) {
            case ROLE_TAG -> { return String.format("<@&%s>", text); }
            case USER_TAG -> { return String.format("<@%s>", text); }
            case CHANNEL_TAG -> { return String.format("<#%s>", text); }
            case DISCORD_EMOJI -> { return String.format("<:%s>", text); }
            case DISCORD_EMOJI_ANIMATED -> { return String.format("<a:%s>", text); }
        }
        return text;
    }

    public boolean isAnyEmojiKind() {
        return type == TYPE.DISCORD_EMOJI || type == TYPE.DISCORD_EMOJI_ANIMATED || type == TYPE.UNICODE_EMOJI;
    }

    private String get(String start, String end, String input) {
        Pattern pattern = Pattern.compile(String.format("(?<=\\%s)(.*?)(?=\\%s)", start, end));
        Matcher matcher = pattern.matcher(input);
        if(matcher.find())
            return matcher.group();
        return null;
    }
}
