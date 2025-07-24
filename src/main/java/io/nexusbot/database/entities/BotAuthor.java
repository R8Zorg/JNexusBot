package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class BotAuthor {
    @Id
    private long id;

    public BotAuthor(long id) {
        this.id = id;
    }

    public BotAuthor() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BotAuthor{id=" + id + "}";
    }
}
