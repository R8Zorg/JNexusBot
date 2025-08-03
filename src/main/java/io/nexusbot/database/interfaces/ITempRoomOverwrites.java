package io.nexusbot.database.interfaces;

import java.util.Map;

import io.nexusbot.database.entities.TempRoomOverwrites;

public interface ITempRoomOverwrites {
    Map<String, Object> getSettings(long ownerId);
    void saveOrUpdate(TempRoomOverwrites voiceChannelOverwrites);
}
