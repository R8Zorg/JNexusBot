package io.nexusbot.modules.listeners.tempRooms.onGuildVoiceUpdate;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.utils.MessageActionUtil;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnLeftTempRoom extends ListenerAdapter {
    private TempRoomService tempRoomService = new TempRoomService();

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() == null) {
            return;
        }
        VoiceChannel leftChannel = (VoiceChannel) event.getChannelLeft();

        TempRoom tempRoom = tempRoomService.get(leftChannel.getIdLong());
        if (tempRoom == null) {
            return;
        }

        if (leftChannel.getMembers().isEmpty()) {
            leftChannel.delete().queue();
            if (tempRoom.getLogMessageId() != null) {
                MessageActionUtil.deleteInfoMessage(event.getGuild(), tempRoom.getChannelLogId(),
                        tempRoom.getLogMessageId());
            }
            tempRoomService.remove(tempRoom);
            return;
        } else {
            Long channelId = tempRoom.getChannelLogId();
            Long messageId = tempRoom.getLogMessageId();
            if (channelId != null && messageId != null) {
                MessageActionUtil.updateInfoMessage(event.getGuild(), channelId, messageId,
                        tempRoom, leftChannel);
            }
        }
    }
}
