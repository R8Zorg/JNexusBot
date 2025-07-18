package io.nexusbot.database.interfaces;

import java.util.List;

import io.nexusbot.database.entities.GuildEntity;

public interface IGuildEntityDao {
    GuildEntity get(long guildId);
    List<GuildEntity> getAll();
    void save(GuildEntity guild);
    void remove(GuildEntity guild);
    
    
}
