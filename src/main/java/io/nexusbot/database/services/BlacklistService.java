package io.nexusbot.database.services;

import io.nexusbot.database.dao.AbstractCrudDao;
import io.nexusbot.database.entities.Blacklist;

public class BlacklistService extends AbstractCrudDao<Blacklist, Long> {

    public BlacklistService() {
        super(Blacklist.class);
    }

    public void add(long userId, String reason) {
        Blacklist blacklist = new Blacklist(userId, reason);
        create(blacklist);
    }

    public void delete(long userId) {
        Blacklist blacklist = get(userId);
        if (blacklist != null) {
            super.delete(blacklist);
        }
    }

}
