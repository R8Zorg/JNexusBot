package com.bot.database.services;

import java.util.List;

import com.bot.database.dao.GuildEntityDao;
import com.bot.database.entities.GuildEntity;

public class GuildEntityService {
    private final GuildEntityDao guildDao = new GuildEntityDao();

    public GuildEntityService() {
    }

    public GuildEntity getOwnerId(long guildId) {
        return guildDao.getOwnerId(guildId);
    }

    public List<GuildEntity> getAllGuilds() {
        return guildDao.getAll();
    }

    public void saveGuild(GuildEntity guild) {
        guildDao.save(guild);
    }

    public void updateGuild(GuildEntity guild) {
        guildDao.update(guild);
    }

    public void deleteGuild(GuildEntity guild) {
        guildDao.delete(guild);
    }
}
