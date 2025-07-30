package io.nexusbot.database.services;

import java.util.List;

import org.hibernate.ObjectNotFoundException;

import io.nexusbot.database.dao.GuildRoleDao;
import io.nexusbot.database.entities.GuildRole;
import io.nexusbot.database.interfaces.IGuildRole;

public class GuildRoleService implements IGuildRole {
    private final GuildRoleDao guildRoleDao = new GuildRoleDao();

    public GuildRoleService() {
    }

    @Override
    public GuildRole get(long roleId) throws ObjectNotFoundException {
        return guildRoleDao.get(roleId);
    }

    @Override
    public List<GuildRole> getAll(long guildId) throws ObjectNotFoundException {
        return guildRoleDao.getAll(guildId);
    }

    @Override
    public void add(GuildRole guildRole) {
        guildRoleDao.add(guildRole);
    }

    @Override
    public void remove(GuildRole guildRole) throws IllegalArgumentException {
        guildRoleDao.remove(guildRole);
    }
}
