package io.nexusbot.modules.listeners.tempRooms;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.utils.MessageActionUtil;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnJoinTempRoom extends ListenerAdapter {
    private TempRoomService tempRoomService = new TempRoomService();

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() == null) {
            return;
        }

        VoiceChannel joinedChannel = (VoiceChannel) event.getChannelJoined();
        TempRoom tempRoom = tempRoomService.get(joinedChannel.getIdLong());
        if (tempRoom == null) {
            return;
        }
        Long channelId = tempRoom.getChannelLogId();
        Long messageId = tempRoom.getLogMessageId();
        if (channelId != null && messageId != null) {
            MessageActionUtil.updateInfoMessage(event.getGuild(), channelId, messageId,
                    tempRoom, joinedChannel);
        }
    }
}
