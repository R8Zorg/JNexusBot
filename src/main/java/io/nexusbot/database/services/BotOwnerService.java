package io.nexusbot.database.services;

import java.util.List;

import io.github.r8zorg.jdatools.OwnersRegistry;
import io.nexusbot.database.dao.AbstractCrudDao;
import io.nexusbot.database.entities.BotOwner;

public class BotOwnerService extends AbstractCrudDao<BotOwner, Long> {
    public BotOwnerService() {
        super(BotOwner.class);
    }

    public List<Long> getAllIds() {
        return getAll()
                .stream()
                .map(BotOwner::getId)
                .toList();
    }

    @Override
    public void create(BotOwner botOwner) {
        super.create(botOwner);
        OwnersRegistry.addOwner(botOwner.getId());
    }

    public void add(long userId) {
        BotOwner botOwner = new BotOwner(userId);
        create(botOwner);
    }

    @Override
    public void delete(BotOwner botOwner) {
        super.delete(botOwner);
        OwnersRegistry.removeOwner(botOwner.getId());
    }

    public void delete(long userId) {
        BotOwner botOwner = get(userId);
        delete(botOwner);
    }
}
