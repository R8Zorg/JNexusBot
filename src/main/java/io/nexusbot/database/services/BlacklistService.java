package io.nexusbot.database.services;

import java.util.List;

import io.nexusbot.database.dao.BlacklistDao;
import io.nexusbot.database.entities.Blacklist;
import io.nexusbot.database.interfaces.IBlacklist;

public class BlacklistService implements IBlacklist {
    private BlacklistDao blacklistDao = new BlacklistDao();

    @Override
    public Blacklist get(long userId) {
        return blacklistDao.get(userId);
    }

    public Blacklist getOrCreate(long userId, String reason) {
        Blacklist blacklist = get(userId);
        if (blacklist == null) {
            blacklist = new Blacklist(userId, reason);
        }
        return blacklist;
    }

    @Override
    public List<Blacklist> getAll() {
        return blacklistDao.getAll();
    }

    @Override
    public void add(Blacklist blacklist) {
        blacklistDao.add(blacklist);
    }

    public void add(long userId, String reason) {
        Blacklist blacklist = new Blacklist(userId, reason);
        blacklistDao.add(blacklist);
    }
    @Override
    public void remove(Blacklist blacklist) {
        blacklistDao.remove(blacklist);
    }

    public void remove(long userId) {
        Blacklist blacklist = get(userId);
        if (blacklist != null) {
            blacklistDao.remove(blacklist);
        }
    }

    
}
