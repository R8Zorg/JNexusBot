package io.nexusbot.database.services;

import java.util.NoSuchElementException;

import org.hibernate.ObjectNotFoundException;

import io.nexusbot.database.dao.AbstractCrudDao;
import io.nexusbot.database.entities.GuildInfo;

public class GuildInfoService extends AbstractCrudDao<GuildInfo, Long> {

    public GuildInfoService() {
        super(GuildInfo.class);
    }

    public long getOwnerId(long guildId) throws ObjectNotFoundException, NoSuchElementException {
        GuildInfo guild = get(guildId);
        if (guild == null) {
            throw new NoSuchElementException("Guild with id " + guildId + " not found in database");
        }
        return guild.getOwnerId();
    }
}
