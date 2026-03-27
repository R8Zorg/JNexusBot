package io.nexusbot.database.services;

import java.util.HashMap;

import io.nexusbot.componentsData.ChannelOverrides;
import io.nexusbot.database.dao.AbstractCrudDao;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.entities.TempRoomSettingsPK;

public class TempRoomSettingsService extends AbstractCrudDao<TempRoomSettings, TempRoomSettingsPK> {
    public TempRoomSettingsService() {
        super(TempRoomSettings.class);
    }

    public HashMap<Long, ChannelOverrides> getOverrides(long ownerId, long guildId) {
        TempRoomSettingsPK pk = new TempRoomSettingsPK(ownerId, guildId);
        TempRoomSettings overwrites = get(pk);
        return overwrites != null ? overwrites.getOverrides() : null;
    }

    public void setOverrides(long ownerId, long guildId, HashMap<Long, ChannelOverrides> newOverrides) {
        TempRoomSettingsPK pk = new TempRoomSettingsPK(ownerId, guildId);
        TempRoomSettings tempRoomSettings = get(pk);
        tempRoomSettings.setOverrides(newOverrides);
        saveOrUpdate(tempRoomSettings);
    }

    public TempRoomSettings getOrCreate(long ownerId, long guildId) {
        TempRoomSettingsPK pk = new TempRoomSettingsPK(ownerId, guildId);
        TempRoomSettings tempRoomSettings = get(pk);
        if (tempRoomSettings == null) {
            tempRoomSettings = new TempRoomSettings(pk);
            create(tempRoomSettings);
        }
        return tempRoomSettings;
    }
}
