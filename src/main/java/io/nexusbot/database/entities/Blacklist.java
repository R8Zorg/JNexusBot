package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Blacklist {
    @Id
    private long userId;
    
    private String reason;

    public Blacklist(long id, String reason) {
        this.userId = id;
        this.reason = reason;
    }

    public Blacklist() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long id) {
        this.userId = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Blacklist{id=" + userId + ", reason=" + reason + "}";
    }
}
