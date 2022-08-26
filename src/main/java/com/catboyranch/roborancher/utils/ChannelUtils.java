package com.catboyranch.roborancher.utils;

import com.catboyranch.roborancher.Server;
import net.dv8tion.jda.api.entities.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelUtils {

    public static void sendPrivateMessage(Member member, String message) {
        member.getUser().openPrivateChannel().queue((channel) -> channel.sendMessage(message).queue());
    }

    public static MessageChannel getChannel(Guild guild, String channelID) {
        if(!channelID.isEmpty())
            return guild.getTextChannelById(channelID);
        return null;
    }

    public static void getMessage(Server server, String messageID, IFunction onComplete) {
        List<TextChannel> channels = server.getGuild().getTextChannels();
        final int size = channels.size();
        AtomicInteger checked = new AtomicInteger();
        AtomicBoolean found = new AtomicBoolean(false);

        for(MessageChannel channel : channels) {
            if(found.get())
                break;
            channel.retrieveMessageById(messageID).queue((message) -> {
                //We found our message, avoid further loop
                found.set(true);
                onComplete.run(message);
            }, (failure) -> {
                if(checked.get() >= size && !found.get()) {
                    //We are done, but we have not found the message. Return null.
                    onComplete.run();
                }
                checked.incrementAndGet();
            });
        }
    }

    public static String getChannelTag(String channelID, Server server) {
        if(!Utils.isLong(channelID))
            return channelID;
        GuildChannel guildChannel = server.getGuild().getGuildChannelById(channelID);
        if(guildChannel == null)
            return channelID;
        return guildChannel.getAsMention();
    }

    public static void deleteMessage(Message message) {
        message.delete().queue();
    }

    public static boolean isChannel(Server server, String id) {
        for(Channel channel : server.getGuild().getChannels()) {
            if(channel.getId().equals(id))
                return true;
        }
        return false;
    }
}
