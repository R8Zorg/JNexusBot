package io.nexusbot.modules.listeners.TempRooms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.services.TempRoomCreatorService;
import io.nexusbot.database.services.TempRoomService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnJoinInCreator extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(OnJoinInCreator.class);
    private TempRoomCreatorService creatorService = new TempRoomCreatorService();
    private TempRoomService tempRoomService = new TempRoomService();

    private void saveRoomToDb(VoiceChannel createdRoom, long memberId) {
        TempRoom voiceChannel = new TempRoom(createdRoom.getIdLong());
        voiceChannel.setOwnerId(memberId);
        voiceChannel.setCategoryId(createdRoom.getParentCategoryIdLong());
        tempRoomService.saveOrUpdate(voiceChannel);
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
        if (joinedChannelId == roomCreator.getId()) {
            String membersName = event.getMember().getUser().getName();
            guild.createVoiceChannel(membersName + "'s channel",
                    guild.getCategoryById(roomCreator.getTempRoomCategoryId())).queue(
                            createdRoom -> {
                                saveRoomToDb(createdRoom, event.getMember().getIdLong());
                                guild.moveVoiceMember(event.getMember(), createdRoom).queue();
                            }, _ -> {
                                LOGGER.warn("Couldn't create new voice channel for: {}", membersName);
                            });
        }
    }
}
