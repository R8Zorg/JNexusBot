package io.nexusbot.database.services;

import java.util.List;

import io.nexusbot.componentsData.ChannelOverrides;
import io.nexusbot.database.dao.TempRoomSettingsDao;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.interfaces.ITempRoomSettings;

public class TempRoomSettingsService implements ITempRoomSettings {
    private final TempRoomSettingsDao roomSettings = new TempRoomSettingsDao();

    @Override
    public TempRoomSettings get(long ownerId, long guildId) {
        return roomSettings.get(ownerId, guildId);
    }

    public TempRoomSettings getOrCreate(long ownerId, long guildId) {
        TempRoomSettings roomOverrides = get(ownerId, guildId);
        if (roomOverrides == null) {
            roomOverrides = new TempRoomSettings(ownerId, guildId);
        }
        return roomOverrides;
    }

    public List<ChannelOverrides> getOverrides(long ownerId, long guildId) {
        TempRoomSettings overwrites = get(ownerId, guildId);
        return overwrites != null ? overwrites.getOverrides() : null;
    }

    public void setOverrides(long ownerId, long guildId, List<ChannelOverrides> newOverrides) {
        TempRoomSettings overwrites = get(ownerId, guildId);
        overwrites.setOverrides(newOverrides);
    }

    @Override
    public void saveOrUpdate(TempRoomSettings roomOverrides) {
        roomSettings.saveOrUpdate(roomOverrides);
    }
}
