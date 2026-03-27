package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.GuildRole;

public class GuildRoleService extends CrudDao<GuildRole, Long> {
    public GuildRoleService() {
        super(GuildRole.class);
    }
}
