package io.nexusbot.database.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table
public class GuildInfo {
    @Id
    private long id;

    private long ownerId;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TempVoiceChannelCreator> tempVoiceChannelCreators;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TempVoiceChannel> tempVoiceChannels;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialRole> roles;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuildRole> guildRoles;

    public GuildInfo() {
    }

    public GuildInfo(long id, long ownerId) {
        this.id = id;
        this.ownerId = ownerId;
        roles = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long id) {
        ownerId = id;
    }

    @Override
    public String toString() {
        return "GuildEntity{id=" + id + ", owner_id=" + ownerId + "}";
    }

}
