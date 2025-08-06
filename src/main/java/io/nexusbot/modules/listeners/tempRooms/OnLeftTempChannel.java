package io.nexusbot.modules.listeners.tempRooms;

import java.util.ArrayList;
import java.util.List;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.PermissionOverwrite;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.entities.TempRoomOverwrites;
import io.nexusbot.database.services.TempRoomOverwritesService;
import io.nexusbot.database.services.TempRoomService;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnLeftTempChannel extends ListenerAdapter {
    private TempRoomService tempRoomService = new TempRoomService();
    private TempRoomOverwritesService roomOverwritesService = new TempRoomOverwritesService();

    private void saveRoomOverwrites(VoiceChannel voiceChannel, long ownerId, List<PermissionOverride> newOverwrites) {
        List<PermissionOverwrite> overwrites = new ArrayList<>();
        newOverwrites.forEach(po -> {
            String id = po.getId();
            String type = po.isMemberOverride() ? "member" : "role";
            long allow = po.getAllowedRaw();
            long deny = po.getDeniedRaw();

            overwrites.add(new PermissionOverwrite(id, type, allow, deny));
        });
        TempRoomOverwrites roomOverwrites = roomOverwritesService.getOrCreate(ownerId, voiceChannel.getGuild().getIdLong());
        roomOverwrites.setOverwrites(overwrites);
        roomOverwritesService.saveOrUpdate(roomOverwrites);
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
            saveRoomOverwrites(leftChannel, tempRoom.getOwnerId(), leftChannel.getPermissionOverrides());
            leftChannel.delete().queue();
            if (tempRoom.getLogMessageId() != null) {
                // TODO: delete message
            }
            tempRoomService.remove(tempRoom);
            return;
        }
    }
}
