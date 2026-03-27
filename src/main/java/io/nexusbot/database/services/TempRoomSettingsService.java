package io.nexusbot.database.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.nexusbot.componentsData.ChannelOverrides;
import io.nexusbot.database.dao.AbstractCrudDao;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.entities.TempRoomSettingsPK;

public class TempRoomSettingsService extends AbstractCrudDao<TempRoomSettings, TempRoomSettingsPK> {
    public TempRoomSettingsService() {
        super(TempRoomSettings.class);
    }

    public Map<Long, ChannelOverrides> getOverrides(TempRoomSettingsPK pk) {
        TempRoomSettings settings = get(pk);
        return settings != null
                ? Collections.unmodifiableMap(settings.getOverrides())
                : Collections.emptyMap();
    }

    public Map<Long, ChannelOverrides> getOverrides(long ownerId, long guildId) {
        return getOverrides(new TempRoomSettingsPK(ownerId, guildId));
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
