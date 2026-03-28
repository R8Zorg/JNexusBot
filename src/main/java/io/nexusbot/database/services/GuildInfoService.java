package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.GuildInfo;

public class GuildInfoService extends CrudDao<GuildInfo, Long> {
    public GuildInfoService() {
        super(GuildInfo.class);
    }
}
