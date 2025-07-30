package io.nexusbot.database.interfaces;

import java.util.List;

import io.nexusbot.database.entities.GuildRole;

public interface IGuildRole {
    GuildRole get(long roleId);
    List<GuildRole> getAll(long guildId);
    void add(GuildRole guildRole);
    void remove(GuildRole guildRole);
}
