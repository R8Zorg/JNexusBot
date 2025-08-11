package io.nexusbot.database.entities;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class TempRoomSettingsPK implements Serializable {
    protected long ownerId;
    protected long guildId;

    public TempRoomSettingsPK(long ownerId, long guildId) {
        this.ownerId = ownerId;
        this.guildId = guildId;
    }

    public TempRoomSettingsPK() {
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (ownerId ^ (ownerId >>> 32));
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
        TempRoomSettingsPK other = (TempRoomSettingsPK) obj;
        if (ownerId != other.ownerId) {
            return false;
        }
        if (guildId != other.guildId) {
            return false;
        }
        return true;
    }
}
