package io.nexusbot.utils;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nexusbot.componentsData.TempRoomPermissions;
import io.nexusbot.componentsData.TempRoomSettings;
import io.nexusbot.database.entities.TempRoom;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Thumbnail;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.Builder;

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

    public static void sendInitialMessage(GuildVoiceUpdateEvent event, VoiceChannel createdRoom, boolean isCustom) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Добро пожаловать в Вашу личную комнату")
                .setColor(Color.CYAN)
                .setDescription("Владелец: " + event.getMember().getAsMention()
                        + "\n\nВоспользуйтесь списками ниже для изменения и сохранения настроек комнаты")
                .build();

        Builder roomSettingsMenuBuilder = StringSelectMenu.create(TempRoomSettings.id)
                .addOption("Статус", TempRoomSettings.status, "Изменить статус канала")
                .addOption("Лимит", TempRoomSettings.limit, "Изменить лимит комнаты")
                .addOption("Битрейт", TempRoomSettings.bitrate, "Изменить битрейт канала")
                .addOption("18+", TempRoomSettings.nsfw, "Поставить/убрать возрастное ограничение канала")
                .addOption("Стать владельцем комнаты", TempRoomSettings.claim,
                        "Загрузить настройки комнаты для нового владельца");

        Builder roomPermissionsMenuBuilder = StringSelectMenu.create(TempRoomPermissions.id)
                .addOption("Заблокировать доступ", TempRoomPermissions.reject, "Выгнать участника и запретить вход в канал")
                .addOption("Разблокировать доступ", TempRoomPermissions.permit, "Убрать запрет на вход в канал участнику")
                .addOption("Выгнать", TempRoomPermissions.kick, "Выгнать участника")
                .addOption("Закрыть", TempRoomPermissions.lock, "Закрыть вход в комнату")
                .addOption("Открыть", TempRoomPermissions.unlock, "Открыть вход в комнату")
                .addOption("Разрешить вход", TempRoomPermissions.accept, "Разрешить вход в закрытый канал")
                .addOption("Запретить вход", TempRoomPermissions.deny, "Убрать разрешение на вход в закрытый канал");

        if (isCustom) {
            roomSettingsMenuBuilder.addOption("Название", TempRoomSettings.name, "Изменить название комнаты");

            roomPermissionsMenuBuilder.addOption("Скрыть", TempRoomPermissions.ghost, "Скрыть комнату от участников");
            roomPermissionsMenuBuilder.addOption("Показать", TempRoomPermissions.unghost, "Показать комнату для участников");
        }
        createdRoom.sendMessageEmbeds(embed)
                .addActionRow(roomSettingsMenuBuilder.build())
                .addActionRow(roomPermissionsMenuBuilder.build())
                .queue();
    }
}
