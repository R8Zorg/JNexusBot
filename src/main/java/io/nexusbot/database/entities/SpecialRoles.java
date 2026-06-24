package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class SpecialRoles {
    @Id
    private long guildId;

    private Long muteRoleId;

    public SpecialRoles() {
    }

    public SpecialRoles(long guildId) {
        setGuildId(guildId);
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public long getMuteRoleId() {
        return muteRoleId;
    }

    public void setMuteRoleId(long mutedRoleId) {
        this.muteRoleId = mutedRoleId;
    }
}
