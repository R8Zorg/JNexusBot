package io.nexusbot.modules.listeners.tempRooms.onGuildVoiceUpdate;

import java.awt.Color;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
import io.nexusbot.utils.EmbedUtil;
import io.nexusbot.utils.MembersUtil;
import io.nexusbot.utils.MessageActionUtil;
import io.nexusbot.utils.OverridesUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

@EventListeners
public class OnJoinInCreator extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(OnJoinInCreator.class);
    private TempRoomCreatorService creatorService = new TempRoomCreatorService();
    private TempRoomService tempRoomService = new TempRoomService();
    private TempRoomSettingsService roomSettingsService = new TempRoomSettingsService();

    private void deleteSentInfoMessage(Guild guild, TempRoom tempRoom) {
        Long channelId = tempRoom.getChannelLogId();
        Long messageId = tempRoom.getLogMessageId();
        MessageActionUtil.deleteInfoMessage(guild, channelId, messageId);
    }

    private void deleteRoomFromDbAndGuild(VoiceChannel createdRoom) {
        TempRoom tempRoom = tempRoomService.get(createdRoom.getIdLong());
        if (tempRoom != null) {
            tempRoomService.remove(tempRoom);
        }
        createdRoom.delete().queue(success -> {
        }, error -> {
        });
    }

    private MessageEmbed generateInitialEmbedMessage(VoiceChannel voiceChannel, TempRoomCreator roomCreator,
            Member member,
            Color color, String iconUrl) {
        String userLimit = roomCreator.getUserLimit() == 0 ? "∞" : String.valueOf(roomCreator.getUserLimit());
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("**Участники:**")
                .setColor(color)
                .addField("", "【1】" + member.getAsMention() + "\n", false)
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
            TempRoomCreator roomCreator, Role neededRole, TempRoom tempRoom,
            TextChannel infoChannel) {
        Color color = Color.decode("#2f3136");
        String iconUrl = null;
        if (neededRole != null) {
            color = neededRole.getColor();
            iconUrl = neededRole.getIcon() != null ? neededRole.getIcon().getIconUrl() : null;

        }

        MessageEmbed embed = generateInitialEmbedMessage(createdRoom, roomCreator, event.getMember(), color,
                iconUrl);
        try {
            infoChannel.sendMessageEmbeds(embed).queue(sentMessage -> {
                tempRoom.setChannelLogId(roomCreator.getLogChannelId());
                tempRoom.setLogMessageId(sentMessage.getIdLong());
                tempRoomService.saveOrUpdate(tempRoom);
            });
        } catch (InsufficientPermissionException e) {
            tempRoomService.saveOrUpdate(tempRoom);
            EmbedUtil.sendEmbed(createdRoom,
                    "Нет прав для отправки сообщения в канал " + infoChannel.getAsMention() + "(`"
                            + infoChannel.getIdLong() + ")`",
                    Color.RED);
            return;
        }
    }

    private void sendRoleNotFoundMessage(GuildVoiceUpdateEvent event, VoiceChannel createdRoom,
            TempRoomCreator roomCreator, List<Long> neededRolesIds) {
        String roleNotFoundMessage = roomCreator.getRoleNotFoundMessage();
        String rolesMention = "";
        for (Long roleId : neededRolesIds) {
            rolesMention += "<@&" + roleId + ">\n";
        }
        if (roleNotFoundMessage == null) {
            roleNotFoundMessage = event.getMember().getAsMention()
                    + ", у Вас не была найдена ни одна из следующий ролей для этого канала:"
                    + rolesMention;
        }
        try {
            createdRoom.sendMessage(roleNotFoundMessage).queue(success -> {
            }, error -> LOGGER.warn("Не удалось отправить сообщение об отсутствии нужной роли"));
        } catch (InsufficientPermissionException e) {
            LOGGER.warn("Недостаточно прав для отправки сообщения в голосовой канал: {}", e.getMessage());
        }
    }

    private void onceMemberMoved(GuildVoiceUpdateEvent event, VoiceChannel createdRoom, TempRoomCreator roomCreator,
            TempRoomSettings settings, List<Long> neededRolesIds) {
        CompletableFuture.runAsync(() -> {
            if (!isMemberInRoom(event, createdRoom)) {
                TempRoom tempRoom = tempRoomService.get(createdRoom.getIdLong());
                if (tempRoom != null) {
                    deleteSentInfoMessage(event.getGuild(), tempRoom);
                }
                deleteRoomFromDbAndGuild(createdRoom);
                return;
            }
        }, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));

        if (settings.getStatus() != null) {
            createdRoom.modifyStatus(settings.getStatus()).queue();
        }

        Role neededRole = getNeededRole(event, neededRolesIds);
        TempRoom tempRoom = new TempRoom(
                createdRoom.getIdLong(), event.getMember().getIdLong(), createdRoom.getParentCategoryIdLong());

        Long infoChannelId = roomCreator.getLogChannelId();
        if (infoChannelId != null) {
            TextChannel infoChannel = event.getGuild().getChannelById(TextChannel.class, infoChannelId);
            if (infoChannel == null) {
                EmbedUtil.sendEmbed(createdRoom,
                        "Не удалось отправить сообщение в инфо-канал: канал удалён или его нет в базе данных.",
                        Color.RED);
                tempRoomService.saveOrUpdate(tempRoom);
            } else {
                sendAndSaveInfoMessageAndRoom(event, createdRoom, roomCreator,
                        neededRole, tempRoom, infoChannel);
            }
        } else {
            tempRoomService.saveOrUpdate(tempRoom);
        }

        boolean isCustom = false;
        if (roomCreator.getChannelMode().equals(ChannelMode.custom)) {
            isCustom = true;
        }
        MessageActionUtil.sendInitialMessage(event, createdRoom, isCustom);

        if (roomCreator.isRoleNeeded() && !neededRolesIds.isEmpty() && neededRole == null) {
            sendRoleNotFoundMessage(event, createdRoom, roomCreator, neededRolesIds);
        }
    }

    private void addMemberOverrides(VoiceChannel createdRoom, GuildVoiceUpdateEvent event, Member member) {
        EnumSet<Permission> permissions = EnumSet.of(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT,
                Permission.VOICE_MOVE_OTHERS, Permission.VOICE_SET_STATUS,
                Permission.MANAGE_CHANNEL, Permission.VOICE_STREAM);
        createdRoom.upsertPermissionOverride(member)
                .grant(permissions).queue();

    }

    private void updateRoomOverrides(GuildVoiceUpdateEvent event, VoiceChannel createdRoom,
            TempRoomCreator roomCreator, TempRoomSettings roomSettings) {
        addMemberOverrides(createdRoom, event, event.getGuild().getMember(event.getJDA().getSelfUser()));
        HashMap<Long, ChannelOverrides> initialOverrides = OverridesUtil.serrializeOverrides(
                event.getGuild().getCategoryById(roomCreator.getTempRoomCategoryId()).getPermissionOverrides());
        HashMap<Long, ChannelOverrides> roomOverrides = roomSettings.getOverrides();
        if (!roomOverrides.isEmpty()) {
            if (roomCreator.getChannelMode().equals(ChannelMode.custom)) {
                OverridesUtil.updateChannelOverrides(createdRoom, roomOverrides, initialOverrides,
                        event.getGuild().getPublicRole().getIdLong());
            } else {
                OverridesUtil.updateChannelOverrides(createdRoom, roomOverrides, initialOverrides);
            }
        }
        addMemberOverrides(createdRoom, event, event.getMember());
    }

    private ChannelAction<VoiceChannel> createNewRoom(GuildVoiceUpdateEvent event, TempRoomSettings roomSettings,
            TempRoomCreator roomCreator, List<Long> neededRolesIds) {
        String roomName = roomSettings.getName() != null ? roomSettings.getName()
                : event.getMember().getUser().getName() + "'s channel";

        boolean isRoleNeeded = creatorService.isRoleNeeded(event.getChannelJoined().getIdLong());
        if (isRoleNeeded && !neededRolesIds.isEmpty()) {
            Member member = event.getMember();
            for (Role role : member.getRoles()) {
                if (neededRolesIds.contains(role.getIdLong())) {
                    roomName = "【🏆】" + role.getName();
                    break;
                }
            }
        }

        int userLimit = roomSettings.getUserLimit();

        if (roomCreator.getChannelMode().equals(ChannelMode.basic)) {
            userLimit = roomCreator.getUserLimit();
            if (roomCreator.getDefaultTempChannelName() != null) {
                roomName = roomCreator.getDefaultTempChannelName();
            }
        }
        Guild guild = event.getGuild();
        ChannelAction<VoiceChannel> newRoom = guild
                .createVoiceChannel(roomName, guild.getCategoryById(roomCreator.getTempRoomCategoryId()))
                .setUserlimit(userLimit)
                .setNSFW(roomSettings.isNsfw())
                .setBitrate(roomSettings.getBitrate());

        return newRoom;

    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() == null) {
            return;
        }
        if (MembersUtil.inBlacklist(event.getMember().getIdLong())) {
            event.getGuild().moveVoiceMember(event.getMember(), null).queue();
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
        try {
            ChannelAction<VoiceChannel> newRoom = createNewRoom(event, roomSettings, roomCreator, neededRolesIds);
            newRoom.queue(createdRoom -> {
                try {
                    updateRoomOverrides(event, createdRoom, roomCreator, roomSettings);
                } catch (InsufficientPermissionException e) {
                    LOGGER.warn("Не удалось обновить права канала: {}", e.getMessage());
                    createdRoom.delete().queue();
                    return;
                }
                try {
                    event.getGuild().moveVoiceMember(event.getMember(), createdRoom).queue(success -> {
                        onceMemberMoved(event, createdRoom, roomCreator, roomSettings, neededRolesIds);
                    }, error -> {
                        LOGGER.warn("Не удалось переместить участника {} в канал {}", membersName,
                                createdRoom.getName());
                        createdRoom.delete().queue();
                    });
                } catch (InsufficientPermissionException e) {
                    LOGGER.warn("Нет прав для перемещения учасника {} в канал: {}", membersName,
                            createdRoom.getName());
                    createdRoom.delete().queue();
                } catch (IllegalStateException e) {
                    LOGGER.warn("Не удалось переместить пользователя: пользователя нет в канале");
                    createdRoom.delete().queue();
                }
            }, error -> {
                LOGGER.warn("Не удалось создать голосовой канал для пользователя {}: {}", membersName,
                        error.getMessage());
            });
        } catch (PermissionException e) {
            LOGGER.warn("Не удалось создать комнату по причине: " + e.getMessage());
        }
    }
}
