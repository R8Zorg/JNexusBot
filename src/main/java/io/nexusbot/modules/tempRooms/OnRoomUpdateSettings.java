package io.nexusbot.modules.tempRooms;

import java.util.Map;
import java.util.function.BiConsumer;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.utils.TempRoomUtil;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateBitrateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateUserLimitEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateVoiceStatusEvent;
import net.dv8tion.jda.api.events.channel.update.GenericChannelUpdateEvent;
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnRoomUpdateSettings extends ListenerAdapter {
    private TempRoomService roomService = new TempRoomService();

    private final Map<Class<?>, BiConsumer<VoiceChannel, Long>> CHANNEL_SETTINGS_HANDLERS = Map.of(
            ChannelUpdateBitrateEvent.class, TempRoomUtil::saveBitrate,
            ChannelUpdateNSFWEvent.class, TempRoomUtil::saveNsfw,
            ChannelUpdateNameEvent.class, TempRoomUtil::saveName,
            ChannelUpdateUserLimitEvent.class, TempRoomUtil::saveUserLimit,
            ChannelUpdateVoiceStatusEvent.class, TempRoomUtil::saveVoiceStatus);

    private final Map<Class<?>, BiConsumer<Long, PermissionOverride>> CHANNEL_OVERRIDES_HANDLERS = Map.of(
            PermissionOverrideCreateEvent.class, TempRoomUtil::saveOverride,
            PermissionOverrideDeleteEvent.class, TempRoomUtil::saveOverride,
            PermissionOverrideUpdateEvent.class, TempRoomUtil::saveOverride);

    private void handleChannelUpdate(GenericChannelUpdateEvent<?> event, BiConsumer<VoiceChannel, Long> action) {
        TempRoom room = roomService.get(event.getChannel().getIdLong());
        if (room != null) {
            action.accept(event.getChannel().asVoiceChannel(), room.getOwnerId());
        }
    }

    private void handleChannelOverrideEdit(GenericPermissionOverrideEvent event,
            BiConsumer<Long, PermissionOverride> action) {
        TempRoom room = roomService.get(event.getChannel().getIdLong());
        if (room != null) {
            action.accept(room.getOwnerId(), event.getPermissionOverride());
        }
    }

    @Override
    public void onGenericChannelUpdate(GenericChannelUpdateEvent<?> event) {
        var consumer = CHANNEL_SETTINGS_HANDLERS.get((event.getClass()));
        if (consumer != null) {
            handleChannelUpdate(event, consumer);
        }
    }

    @Override
    public void onGenericPermissionOverride(GenericPermissionOverrideEvent event) {
        var consumer = CHANNEL_OVERRIDES_HANDLERS.get(event.getClass());
        if (consumer != null) {
            handleChannelOverrideEdit(event, consumer);
        }
    }
}
