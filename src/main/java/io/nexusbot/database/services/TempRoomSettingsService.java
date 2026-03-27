package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.entities.TempRoomSettingsPK;

public class TempRoomSettingsService extends CrudDao<TempRoomSettings, TempRoomSettingsPK> {
    public TempRoomSettingsService() {
        super(TempRoomSettings.class);
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
