package io.nexusbot.modules.listeners.tempRooms;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.PermissionOverwrite;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.services.TempRoomCreatorService;
import io.nexusbot.database.services.TempRoomOverwritesService;
import io.nexusbot.database.services.TempRoomService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnJoinInCreator extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(OnJoinInCreator.class);
    private TempRoomCreatorService creatorService = new TempRoomCreatorService();
    private TempRoomService tempRoomService = new TempRoomService();
    private TempRoomOverwritesService overwritesService = new TempRoomOverwritesService();

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

    private Long sendLogMessage(TextChannel logChannel, VoiceChannel voiceChannel) {
        // TODO:
        // logChannel.sendMessageEmbeds(embeds)
        return 0L;

    }

    private void setRoomOverwrites(GuildVoiceUpdateEvent event, VoiceChannel voiceChannel) {
        long ownerId = event.getMember().getIdLong();
        long guildId = event.getGuild().getIdLong();
        List<PermissionOverwrite> roomOverwrites = overwritesService.getOverwrites(ownerId, guildId);
        if (roomOverwrites == null || roomOverwrites.isEmpty()) {
            return;
        }

        for (PermissionOverwrite permissionOverwrite : roomOverwrites) {
            String id = permissionOverwrite.getId();
            String type = permissionOverwrite.getType();
            long allow = permissionOverwrite.getAllow();
            long deny = permissionOverwrite.getDeny();

            if (type.equals("role")) {
                Role role = event.getGuild().getRoleById(id);
                if (role != null) {
                    voiceChannel.upsertPermissionOverride(role)
                            .setAllowed(allow)
                            .setDenied(deny)
                            .queue();
                }
            } else if (type.equals("member")) {
                Member member = event.getGuild().getMemberById(id);
                if (member != null) {
                    voiceChannel.upsertPermissionOverride(member)
                            .setAllowed(allow)
                            .setDenied(deny)
                            .queue();
                }
            }
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
        Long logChanelId = roomCreator.getLogChannelId();
        try {
            guild.createVoiceChannel(membersName + "'s channel",
                    guild.getCategoryById(roomCreator.getTempRoomCategoryId())).queue(
                            createdRoom -> {
                                setRoomOverwrites(event, createdRoom);
                                guild.moveVoiceMember(event.getMember(), createdRoom).queue();
                                if (logChanelId != null) {
                                    Long sentLogMessageId = sendLogMessage(
                                            event.getGuild().getTextChannelById(logChanelId),
                                            createdRoom);
                                    saveRoomToDb(createdRoom, event.getMember().getIdLong(), logChanelId,
                                            sentLogMessageId);
                                } else {
                                    saveRoomToDb(createdRoom, event.getMember().getIdLong());
                                }
                            }, _ -> {
                                LOGGER.warn("Couldn't create new voice channel for: {}", membersName);
                            });
        } catch (PermissionException e) {
            LOGGER.warn("Не удалось создать комнату по причине: " + e.getMessage());
        }
    }
}
