package com.bot.database.interfaces;

import java.util.List;

import com.bot.database.entities.GuildEntity;

public interface IGuildEntityDao {
    GuildEntity getOwnerId(long guildId);
    List<GuildEntity> getAll();
    void save(GuildEntity guild);
    void update(GuildEntity guild);
    void delete(GuildEntity guild);
    
    
}
