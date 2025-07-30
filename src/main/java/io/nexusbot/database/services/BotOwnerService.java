package io.nexusbot.database.services;

import java.util.List;

import org.hibernate.ObjectNotFoundException;

import io.github.r8zorg.jdatools.OwnersRegistry;
import io.nexusbot.database.dao.BotOwnerDao;
import io.nexusbot.database.entities.BotOwner;
import io.nexusbot.database.interfaces.IBotOwner;

public class BotOwnerService implements IBotOwner {
    private final BotOwnerDao botOwnerDao = new BotOwnerDao();

    public BotOwnerService() {
    }

    @Override
    public BotOwner get(long userId) throws ObjectNotFoundException {
        return botOwnerDao.get(userId);
    }

    @Override
    public List<BotOwner> getAll() {
        return botOwnerDao.getAll();
    }

    public List<Long> getAllIds() {
        return botOwnerDao.getAll()
                .stream()
                .map(BotOwner::getId)
                .toList();
    }

    @Override
    public void add(BotOwner botOwner) {
        botOwnerDao.add(botOwner);
        OwnersRegistry.addOwner(botOwner.getId());
    }

    public void add(long userId) {
        BotOwner botOwner = new BotOwner(userId);
        botOwnerDao.add(botOwner);
        OwnersRegistry.addOwner(botOwner.getId());
    }

    @Override
    public void remove(BotOwner botOwner) throws IllegalArgumentException {
        botOwnerDao.remove(botOwner);
        OwnersRegistry.removeOwner(botOwner.getId());
    }

    public void remove(long userId) throws IllegalArgumentException {
        BotOwner botOwner = botOwnerDao.get(userId);
        botOwnerDao.remove(botOwner);
        OwnersRegistry.removeOwner(botOwner.getId());
    }
}
