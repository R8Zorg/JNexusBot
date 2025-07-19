package io.nexusbot.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "guilds")
public class GuildEntity {
    @Id
    private long id;
    @Column
    private long ownerId;

    public GuildEntity() {

    }

    public GuildEntity(long id, long ownerId) {
        this.id = id;
        this.ownerId = ownerId;
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
