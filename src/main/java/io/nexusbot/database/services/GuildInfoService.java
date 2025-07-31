package io.nexusbot.database.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.hibernate.ObjectNotFoundException;

import io.nexusbot.database.dao.GuildInfoDao;
import io.nexusbot.database.entities.GuildInfo;
import io.nexusbot.database.interfaces.IGuildInfo;

public class GuildInfoService implements IGuildInfo {
    private final GuildInfoDao guildDao = new GuildInfoDao();

    public GuildInfoService() {
    }

    @Override
    public GuildInfo get(long guildId) throws ObjectNotFoundException {
        return guildDao.get(guildId);
    }

    public long getOwnerId(long guildId) throws ObjectNotFoundException, NoSuchElementException {
        GuildInfo guild = guildDao.get(guildId);
        if (guild == null) {
            throw new NoSuchElementException("Guild with id " + guildId + " not found in database");
        }
        return guild.getOwnerId();
    }

    @Override
    public List<GuildInfo> getAll() {
        return guildDao.getAll();
    }

    @Override
    public void saveOrUpdate(GuildInfo guild) {
        guildDao.saveOrUpdate(guild);
    }

    @Override
    public void remove(GuildInfo guild) throws IllegalArgumentException {
        guildDao.remove(guild);
    }
}
