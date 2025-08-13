package io.nexusbot.modules.listeners.tempRooms;

import java.util.List;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.ChannelOverrides;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.database.services.TempRoomSettingsService;
import io.nexusbot.utils.MessageActionUtil;
import io.nexusbot.utils.OverridesUtil;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnLeftTempChannel extends ListenerAdapter {
    private TempRoomService tempRoomService = new TempRoomService();
    private TempRoomSettingsService roomOverridesService = new TempRoomSettingsService();

    private void saveRoomSettings(VoiceChannel voiceChannel, long ownerId, List<PermissionOverride> newOverrides) {
        List<ChannelOverrides> overwrites = OverridesUtil.serrializeOverrides(newOverrides);
        TempRoomSettings roomOverrides = roomOverridesService.getOrCreate(ownerId, voiceChannel.getGuild().getIdLong());
        roomOverrides.setOverrides(overwrites);
        roomOverridesService.saveOrUpdate(roomOverrides);
    }

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
            saveRoomSettings(leftChannel, tempRoom.getOwnerId(), leftChannel.getPermissionOverrides());
            leftChannel.delete().queue();
            if (tempRoom.getLogMessageId() != null) {
                MessageActionUtil.deleteInfoMessage(event.getGuild(), tempRoom.getChannelLogId(),
                        tempRoom.getLogMessageId());
            }
            tempRoomService.remove(tempRoom);
            return;
        }
        // CompletableFuture.runAsync(() -> {
        // }, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
    }
}
