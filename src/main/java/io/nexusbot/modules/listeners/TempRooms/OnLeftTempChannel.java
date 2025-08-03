package io.nexusbot.modules.listeners.TempRooms;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.services.TempRoomService;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnLeftTempChannel extends ListenerAdapter {
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

        if (leftChannel.getParentCategoryIdLong() == tempRoom.getCategoryId()) {
            if (!leftChannel.getMembers().isEmpty()) {
                return;
            }
            leftChannel.delete().queue();
            tempRoomService.remove(tempRoom);
        }
    }
}
