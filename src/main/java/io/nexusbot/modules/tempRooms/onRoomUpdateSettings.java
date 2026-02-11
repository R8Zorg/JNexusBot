package io.nexusbot.modules.tempRooms;

import java.util.function.BiConsumer;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.utils.TempRoomUtil;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateBitrateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateUserLimitEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateVoiceStatusEvent;
import net.dv8tion.jda.api.events.channel.update.GenericChannelUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class onRoomUpdateSettings extends ListenerAdapter {
    private TempRoomService roomService = new TempRoomService();

    private void handleChannelUpdate(GenericChannelUpdateEvent<?> event, BiConsumer<VoiceChannel, Long> action) {
        TempRoom room = roomService.get(event.getChannel().getIdLong());
        if (room != null) {
            action.accept(event.getChannel().asVoiceChannel(), room.getOwnerId());
        }
    }

    @Override
    public void onChannelUpdateBitrate(ChannelUpdateBitrateEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveBitrate);
    }

    @Override
    public void onChannelUpdateNSFW(ChannelUpdateNSFWEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveNsfw);
    }

    @Override
    public void onChannelUpdateName(ChannelUpdateNameEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveName);
    }

    @Override
    public void onChannelUpdateUserLimit(ChannelUpdateUserLimitEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveUserLimit);
    }

    @Override
    public void onChannelUpdateVoiceStatus(ChannelUpdateVoiceStatusEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveVoiceStatus);
    }
}
