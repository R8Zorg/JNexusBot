package io.nexusbot.database.interfaces;

import io.nexusbot.database.entities.TempRoomOverwrites;
import io.nexusbot.database.entities.TempRoomOverwritesPK;

public interface ITempRoomOverwrites {
    TempRoomOverwrites get(long ownerId, long guildId);
    void saveOrUpdate(TempRoomOverwrites voiceChannelOverwrites);
}
