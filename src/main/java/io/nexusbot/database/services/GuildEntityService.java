package io.nexusbot.database.services;

import java.util.List;
import java.util.NoSuchElementException;

import io.nexusbot.database.dao.GuildEntityDao;
import io.nexusbot.database.entities.GuildInfo;

import org.hibernate.ObjectNotFoundException;

public class GuildEntityService {
    private final GuildEntityDao guildDao = new GuildEntityDao();

    public GuildEntityService() {
    }

    public GuildInfo get(long guildId) throws ObjectNotFoundException {
        return guildDao.get(guildId);
    }

    public GuildInfo get(String guildId) throws ObjectNotFoundException {
        long guildIdLong = Long.parseLong(guildId);
        return guildDao.get(guildIdLong);
    }

    public long getOwnerId(long guildId) throws ObjectNotFoundException {
        GuildInfo guild = guildDao.get(guildId);
        if (guild == null) {
            throw new NoSuchElementException("Guild with id " + guildId + " not found in database");
        }
        return guild.getOwnerId();
    }

    public List<GuildInfo> getAll() {
        return guildDao.getAll();
    }

    public void save(GuildInfo guild) {
        guildDao.save(guild);
    }

    public void remove(GuildInfo guild) throws IllegalArgumentException {
        guildDao.remove(guild);
    }
}
