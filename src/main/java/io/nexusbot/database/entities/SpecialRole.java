package io.nexusbot.database.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table
public class SpecialRole {
    @Id
    private long guildId;

    @ManyToOne()
    @MapsId
    @JoinColumn(name = "guild_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GuildInfo guild;

    private long mutedRoleId;

    public SpecialRole() {
    }

    public SpecialRole(GuildInfo guild, long mutedRoleId) {
        this.guild = guild;
        this.mutedRoleId = mutedRoleId;
    }

    public GuildInfo getGuild() {
        return guild;
    }

    public void setGuild(GuildInfo guild) {
        this.guild = guild;
    }

    public long getMutedRoleId() {
        return mutedRoleId;
    }

    public void setMutedRoleId(long mutedRoleId) {
        this.mutedRoleId = mutedRoleId;
    }
}
