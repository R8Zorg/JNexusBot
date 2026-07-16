package io.nexusbot.database.entities;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class VerificationPK implements Serializable {
    private long memberId;
    private long guildId;

    public VerificationPK() {
    }

    public VerificationPK(long memberId, long guildId) {
        this.memberId = memberId;
        this.guildId = guildId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
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
        result = prime * result + (int) (memberId ^ (memberId >>> 32));
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
        VerificationPK other = (VerificationPK) obj;
        if (memberId != other.memberId) {
            return false;
        }
        if (guildId != other.guildId) {
            return false;
        }
        return true;
    }
}
