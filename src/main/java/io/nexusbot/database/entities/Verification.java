package io.nexusbot.database.entities;

import java.time.OffsetDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class Verification {
    @EmbeddedId
    private VerificationPK verificationPK;

    private int memberAttempts = 0;
    private OffsetDateTime memberTimeout;

    public Verification() {
    }

    public Verification(long memberId, long guildId) {
        verificationPK = new VerificationPK(memberId, guildId);
    }

    public Verification(VerificationPK pk) {
        verificationPK = pk;
    }

    public VerificationPK getVerificationPK() {
        return verificationPK;
    }

    public void setVerificationPK(VerificationPK verificationPK) {
        this.verificationPK = verificationPK;
    }

    public int getMemberAttempts() {
        return memberAttempts;
    }

    public void setMemberAttempts(int memberAttempts) {
        this.memberAttempts = memberAttempts;
    }

    public void addMemberAttempts(int value) {
        this.memberAttempts += value;
    }

    public OffsetDateTime getMemberTimeout() {
        return memberTimeout;
    }

    public void setMemberTimeout(OffsetDateTime memberTimeout) {
        this.memberTimeout = memberTimeout;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((verificationPK == null) ? 0 : verificationPK.hashCode());
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
        Verification other = (Verification) obj;
        if (verificationPK == null) {
            if (other.verificationPK != null) {
                return false;
            }
        } else if (!verificationPK.equals(other.verificationPK)) {
            return false;
        }
        return true;
    }
}
