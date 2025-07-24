package io.nexusbot.database.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class GuildInfo {
    @Id
    private long id;

    private long ownerId;

    @OneToOne(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private TempVoiceChannelCreatorRole tempVoiceChannelCreatorRole;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpecialGuildRoles> roles;

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
