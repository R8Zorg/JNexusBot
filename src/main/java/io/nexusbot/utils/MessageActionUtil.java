package io.nexusbot.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MessageActionUtil {
    public static void deleteInfoMessage(Guild guild, Long channelId, Long messageId) {
        if (channelId == null || messageId == null) {
            return;
        }
        TextChannel infoChannel = guild.getChannelById(TextChannel.class, channelId);
        if (infoChannel != null) {
            infoChannel.deleteMessageById(messageId).queue();
        }
    }
}
