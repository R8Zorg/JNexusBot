package io.nexusbot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nexusbot.database.entities.TempRoom;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Thumbnail;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class MessageActionUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageActionUtil.class);

    public static void deleteInfoMessage(Guild guild, Long channelId, Long messageId) {
        if (channelId == null || messageId == null) {
            return;
        }
        TextChannel infoChannel = guild.getChannelById(TextChannel.class, channelId);
        if (infoChannel != null) {
            infoChannel.deleteMessageById(messageId).queue();
        }
    }

    private static MessageEmbed getUpdatedEmbed(MessageEmbed embed, VoiceChannel room) {
        int membersCount = room.getMembers().size();
        String membersFieldValue = "";
        for (int i = 0; i < membersCount; i++) {
            membersFieldValue += "【" + (i + 1) + "】"
                    + room.getMembers().get(i).getAsMention()
                    + "\n";
        }

        String channelLinkFieldValue = "";
        if (room.getUserLimit() != 0 && membersCount >= room.getUserLimit()) {
            channelLinkFieldValue = "\n**❌ Канал заполнен**";
        } else {
            channelLinkFieldValue = "\n**✅ Канал:** " + room.getAsMention();
        }

        String userLimit = room.getUserLimit() == 0 ? "∞" : String.valueOf(room.getUserLimit());
        EmbedBuilder updatedEmbed = new EmbedBuilder()
                .setTitle(embed.getTitle())
                .setColor(embed.getColor())
                .addField("", membersFieldValue, false)
                .addField("", channelLinkFieldValue, false)
                .setFooter("Участников: " + membersCount + "/" + userLimit);

        Thumbnail thumbnail = embed.getThumbnail();
        if (thumbnail != null && thumbnail.getUrl() != null) {
            updatedEmbed.setThumbnail(thumbnail.getUrl());
        }

        return updatedEmbed.build();
    }

    public static void updateInfoMessage(Guild guild, Long channelId, Long messageId, TempRoom tempRoom,
            VoiceChannel voiceChannel) {
        TextChannel channel = guild.getChannelById(TextChannel.class, channelId);
        if (channel == null) {
            return;
        }
        channel.retrieveMessageById(messageId).queue(infoMessage -> {
            MessageEmbed updatedEmbed = getUpdatedEmbed(infoMessage.getEmbeds().get(0), voiceChannel);
            infoMessage.editMessageEmbeds(updatedEmbed).queue(_ -> {
            }, errorResponse -> {
                LOGGER.warn("Не удалось обновить сообщение - нет доступа. Ошибка: {}",
                        errorResponse.getMessage());
            });
        });
    }
}
