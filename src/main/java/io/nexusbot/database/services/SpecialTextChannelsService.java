package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.SpecialTextChannels;

public class SpecialTextChannelsService extends CrudDao<SpecialTextChannels, Long> {
    public SpecialTextChannelsService() {
        super(SpecialTextChannels.class);
    }

    @Override
    public SpecialTextChannels get(Long guildId) {
        return super.get(guildId);
    }

}
