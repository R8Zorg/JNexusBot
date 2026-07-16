package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class AutoSpamRemove {
    @Id
    private long guildId;

    private boolean isTurnOn = false;

    public AutoSpamRemove() {
    }

    public AutoSpamRemove(long guildId, boolean isTurnOn) {
        this.guildId = guildId;
        this.isTurnOn = isTurnOn;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public boolean isTurnOn() {
        return isTurnOn;
    }

    public void setTurnOn(boolean isTurnOn) {
        this.isTurnOn = isTurnOn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (guildId ^ (guildId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AutoSpamRemove other = (AutoSpamRemove) obj;
        if (guildId != other.guildId) {
            return false;
        }
        return true;
    }

}
