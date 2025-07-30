package io.nexusbot.database.interfaces;

import java.util.List;

import io.nexusbot.database.entities.GuildInfo;

public interface IGuildInfo {
    GuildInfo get(long guildId);
    List<GuildInfo> getAll();
    void save(GuildInfo guild);
    void remove(GuildInfo guild);
}
