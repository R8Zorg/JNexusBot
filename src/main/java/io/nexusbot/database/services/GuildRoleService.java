package io.nexusbot.database.services;

import io.nexusbot.database.dao.AbstractCrudDao;
import io.nexusbot.database.entities.GuildRole;

public class GuildRoleService extends AbstractCrudDao<GuildRole, Long> {
    public GuildRoleService() {
        super(GuildRole.class);
    }
}
