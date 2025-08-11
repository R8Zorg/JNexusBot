package io.nexusbot.modules.listeners.tempRooms;

import java.awt.Color;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.ChannelMode;
import io.nexusbot.componentsData.ChannelOverrides;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.services.TempRoomCreatorService;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.database.services.TempRoomSettingsService;
import io.nexusbot.utils.OverridesUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnJoinInCreator extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(OnJoinInCreator.class);
    private TempRoomCreatorService creatorService = new TempRoomCreatorService();
    private TempRoomService tempRoomService = new TempRoomService();
    private TempRoomSettingsService roomSettingsService = new TempRoomSettingsService();

    private void saveRoomToDb(VoiceChannel createdRoom, long memberId, long channelLogId, long sentLogMessageId) {
        TempRoom voiceChannel = new TempRoom(
                createdRoom.getIdLong(), memberId, createdRoom.getParentCategoryIdLong());
        voiceChannel.setChannelLogId(channelLogId);
        voiceChannel.setLogMessageId(sentLogMessageId);
        tempRoomService.saveOrUpdate(voiceChannel);
    }

    private void saveRoomToDb(VoiceChannel createdRoom, long memberId) {
        TempRoom voiceChannel = new TempRoom(
                createdRoom.getIdLong(), memberId, createdRoom.getParentCategoryIdLong());
        tempRoomService.saveOrUpdate(voiceChannel);
    }

    private void deleteRoomFromDbAndGuild(VoiceChannel createdRoom) {
        TempRoom tempRoom = tempRoomService.get(createdRoom.getIdLong());
        if (tempRoom != null) {
            tempRoomService.remove(tempRoom);
        }
        try {
            createdRoom.delete().queue();
        } catch (Exception e) {
        }
    }

    private MessageEmbed generateInitialEmbedMessage(VoiceChannel voiceChannel, TempRoomCreator roomCreator,
            Member member,
            Color color, String iconUrl) {
        String userLimit = roomCreator.getUserLimit() == 0 ? "∞" : String.valueOf(roomCreator.getUserLimit());
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("**Участники:**")
                .setColor(color)
                .addField("", "【1】" + member.getEffectiveName() + "\n", false)
                .addField("", "\n**✅ Канал:** " + voiceChannel.getAsMention(), false)
                .setFooter("Участников: 1/" + userLimit);
        if (iconUrl != null) {
            embedBuilder.setThumbnail(iconUrl);
        }
        return embedBuilder.build();
    }

    private boolean isMemberInRoom(GuildVoiceUpdateEvent event, VoiceChannel createdRoom) {
        if (createdRoom.getMembers().contains(event.getMember())) {
            return true;
        }
        return false;
    }

    private void updateRoomOverrides(VoiceChannel createdRoom, Member member) {
        List<ChannelOverrides> ownerOverrides = roomSettingsService.getOverrides(member.getIdLong(),
                member.getGuild().getIdLong());
        List<ChannelOverrides> initialOverrides = OverridesUtil
                .serrializeOverrides(createdRoom.getPermissionOverrides());

        OverridesUtil.updateChannelOverrides(createdRoom, ownerOverrides);
        OverridesUtil.updateChannelOverrides(createdRoom, initialOverrides);
    }

    private Role getNeededRole(GuildVoiceUpdateEvent event, List<Long> neededRolesIds) {
        List<Role> memberRoles = event.getMember().getRoles();
        for (Long roleId : neededRolesIds) {
            Role neededRole = event.getGuild().getRoleById(roleId);
            if (neededRole != null && memberRoles.contains(neededRole)) {
                return neededRole;
            }
        }
        return null;
    }

    private void sendAndSaveInfoMessageAndRoom(GuildVoiceUpdateEvent event, VoiceChannel createdRoom,
            TempRoomCreator roomCreator, Role neededRole) {
        Color color = Color.decode("#2f3136");
        String iconUrl = null;
        if (neededRole != null) {
            color = neededRole.getColor();
            iconUrl = neededRole.getIcon() != null ? neededRole.getIcon().getIconUrl() : null;
        }

        MessageEmbed embed = generateInitialEmbedMessage(createdRoom, roomCreator, event.getMember(), color,
                iconUrl);

        TextChannel infoChannel = event.getGuild().getChannelById(TextChannel.class, roomCreator.getLogChannelId());
        try {
            infoChannel.sendMessageEmbeds(embed).queue(sentMessage -> {
                saveRoomToDb(createdRoom, event.getMember().getIdLong(), roomCreator.getLogChannelId(),
                        sentMessage.getIdLong());
            });
        } catch (Exception e) {
            LOGGER.warn("Не удалось отправить сообщение в канал {}", infoChannel.getIdLong());
            return;
        }

    }

    private void sendRoleNotFoundMessage(GuildVoiceUpdateEvent event, VoiceChannel createdRoom,
            TempRoomCreator roomCreator, List<Long> neededRolesIds) {
        String roleNotFoundMessage = roomCreator.getRoleNotFoundMessage();
        String rolesMention = "Заданные роли не найдены";
        for (Long roleId : neededRolesIds) {
            rolesMention += "<@&" + roleId + ">\n";
        }
        if (roleNotFoundMessage == null) {
            roleNotFoundMessage = event.getMember().getAsMention()
                    + ", у Вас не была найдена ни одна из следующий ролей для этого канала:"
                    + rolesMention;
        }
        createdRoom.sendMessage(roleNotFoundMessage).queue(_ -> {
        }, _ -> LOGGER.warn("Не удалось отправить сообщение об отсутствии нужной роли")); // There should be exception
                                                                                          // by JDA, not API (use
                                                                                          // try-catch)
    }

    private void onceMemberMoved(GuildVoiceUpdateEvent event, VoiceChannel createdRoom, TempRoomCreator roomCreator,
            TempRoomSettings settings, List<Long> neededRolesIds) {
        LOGGER.info("Once member moved");
        // if
        // (!createdRoom.getGuild().getVoiceChannels().contains(createdRoom.getIdLong())
        // || !isMemberInRoom(event, createdRoom)) {
        // return;
        // }
        if (event.getGuild().getChannelById(VoiceChannel.class, createdRoom.getIdLong()) == null) {
            LOGGER.warn("Канал оказался пустым");
            return;
        }
        if (!isMemberInRoom(event, createdRoom)) {
            LOGGER.warn("Пользователя нет в канале");
            // deleteRoomFromDbAndGuild(createdRoom);
            createdRoom.delete().queue();
            return;
        }

        updateRoomOverrides(createdRoom, event.getMember());
        // LOGGER.info("Overrides updated for new room: {}\n", createdRoom.getName());

        if (settings.getStatus() != null) {
            createdRoom.modifyStatus(settings.getStatus()).queue();
        }

        Role neededRole = getNeededRole(event, neededRolesIds);
        if (roomCreator.getLogChannelId() != null) {
            sendAndSaveInfoMessageAndRoom(event, createdRoom, roomCreator, neededRole);
        } else {
            saveRoomToDb(createdRoom, event.getMember().getIdLong());
        }
        // LOGGER.info("Room saved to DB\n");

        if (roomCreator.isRoleNeeded() && !neededRolesIds.isEmpty() && neededRole == null) {
            sendRoleNotFoundMessage(event, createdRoom, roomCreator, neededRolesIds);
        }
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() == null) {
            return;
        }
        long joinedChannelId = event.getChannelJoined().getIdLong();
        TempRoomCreator roomCreator = creatorService.get(joinedChannelId);
        if (roomCreator == null) {
            return;
        }

        Guild guild = event.getGuild();
        if (guild.getCategoryById(roomCreator.getTempRoomCategoryId()) == null) {
            return;
        }

        String membersName = event.getMember().getUser().getName();
        TempRoomSettings roomSettings = roomSettingsService.getOrCreate(
                event.getMember().getIdLong(), guild.getIdLong());

        List<Long> neededRolesIds = creatorService.getNeededRolesIds(joinedChannelId);
        String roomName = roomSettings.getName() != null ? roomSettings.getName() : membersName + "'s channel";
        int userLimit = roomSettings.getUserLimit();
        if (roomCreator.getChannelMode().equals(ChannelMode.basic)) {
            userLimit = roomCreator.getUserLimit();
            if (roomCreator.getDefaultTempChannelName() != null) {
                roomName = roomCreator.getDefaultTempChannelName();
            }
        }
        try {
            guild.createVoiceChannel(roomName, guild.getCategoryById(roomCreator.getTempRoomCategoryId()))
                    .setUserlimit(userLimit)
                    .setNSFW(roomSettings.isNsfw())
                    .setBitrate(roomSettings.getBitrate())
                    .queue(createdRoom -> {
                        saveRoomToDb(createdRoom, event.getMember().getIdLong());
                        try {
                            event.getGuild().moveVoiceMember(event.getMember(), createdRoom).queue(_ -> {
                                onceMemberMoved(event, createdRoom, roomCreator, roomSettings, neededRolesIds);
                            }, _ -> {
                                LOGGER.warn("Не удалось переместить участника {} в канал {}", membersName,
                                        createdRoom.getName());
                                deleteRoomFromDbAndGuild(createdRoom);
                            });
                        } catch (InsufficientPermissionException e) {
                            LOGGER.warn("Нет прав для перемещения учасника {} в канал: {}", membersName,
                                    createdRoom.getName());
                            deleteRoomFromDbAndGuild(createdRoom);
                        } catch (IllegalStateException e) {
                            deleteRoomFromDbAndGuild(createdRoom);
                        }
                    }, _ -> {
                        LOGGER.warn("Не удалось создать голосовой канал для пользователя {}", membersName);
                    });
        } catch (PermissionException e) {
            LOGGER.warn("Не удалось создать комнату по причине: " + e.getMessage());
        }
    }
}
