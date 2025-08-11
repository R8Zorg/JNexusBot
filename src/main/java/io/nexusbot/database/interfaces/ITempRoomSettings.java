package io.nexusbot.database.interfaces;

import io.nexusbot.database.entities.TempRoomSettings;

public interface ITempRoomSettings {
    TempRoomSettings get(long ownerId, long guildId);
    void saveOrUpdate(TempRoomSettings roomOverrides);
}
