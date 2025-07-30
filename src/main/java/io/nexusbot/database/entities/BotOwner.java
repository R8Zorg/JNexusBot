package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class BotOwner {
    @Id
    private long id;

    public BotOwner(long id) {
        this.id = id;
    }

    public BotOwner() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BotOwner{id=" + id + "}";
    }
}
