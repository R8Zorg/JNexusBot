package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.Verification;
import io.nexusbot.database.entities.VerificationPK;

public class VerificationService extends CrudDao<Verification, VerificationPK> {
    public VerificationService() {
        super(Verification.class);
    }

    public Verification getOrCreate(long memberId, long guildId) {
        VerificationPK pk = new VerificationPK(memberId, guildId);
        Verification verification = get(pk);
        if (verification == null) {
            verification = new Verification(pk);
            create(verification);
        }
        return verification;

    }

}
