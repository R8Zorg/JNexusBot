package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.AutoSpamRemove;

public class AutoSpamRemoveService extends CrudDao<AutoSpamRemove, Long> {
    public AutoSpamRemoveService() {
        super(AutoSpamRemove.class);
    }

    public AutoSpamRemove getOrCreate(long guildId, boolean isTurnOn) {
        AutoSpamRemove autoSpamRemove = get(guildId);
        if (autoSpamRemove == null) {
            autoSpamRemove = new AutoSpamRemove(guildId, isTurnOn);
            create(autoSpamRemove);
        }
        return autoSpamRemove;
    }
}
