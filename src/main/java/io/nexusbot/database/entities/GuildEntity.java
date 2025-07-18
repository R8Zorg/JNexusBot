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
    private long owner_id;

    public GuildEntity() {

    }

    public GuildEntity(long id, long owner_id) {
        this.id = id;
        this.owner_id = owner_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(long id) {
        owner_id = id;
    }

    @Override
    public String toString() {
        return "GuildEntity{id=" + id + ", owner_id=" + owner_id + "}";
    }
}
