package io.nexusbot.database.services;

import java.util.List;
import java.util.NoSuchElementException;

import io.nexusbot.database.dao.GuildEntityDao;
import io.nexusbot.database.entities.GuildEntity;

import org.hibernate.ObjectNotFoundException;

public class GuildEntityService {
    private final GuildEntityDao guildDao = new GuildEntityDao();

    public GuildEntityService() {
    }

    public GuildEntity get(long guildId) throws ObjectNotFoundException {
        return guildDao.get(guildId);
    }

    public GuildEntity get(String guildId) throws ObjectNotFoundException {
        long guildIdLong = Long.parseLong(guildId);
        return guildDao.get(guildIdLong);
    }

    public long getOwnerId(long guildId) throws ObjectNotFoundException {
        GuildEntity guild = guildDao.get(guildId);
        if (guild == null) {
            throw new NoSuchElementException("Guild with id " + guildId + " not found in database");
        }
        return guild.getOwnerId();
    }

    public List<GuildEntity> getAll() {
        return guildDao.getAll();
    }

    public void save(GuildEntity guild) {
        guildDao.save(guild);
    }

    public void remove(GuildEntity guild) throws IllegalArgumentException {
        guildDao.remove(guild);
    }
}
