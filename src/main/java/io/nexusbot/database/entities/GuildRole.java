package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class GuildRole {

    @Id
    private long id;

    private long guildId;

    public GuildRole(long id, long guildId) {
        this.id = id;
        this.guildId = guildId;
    }

    public GuildRole() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }
}
