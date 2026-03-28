package io.nexusbot.database.services;

import io.github.r8zorg.jdatools.OwnersRegistry;
import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.BotOwner;

public class BotOwnerService extends CrudDao<BotOwner, Long> {
    public BotOwnerService() {
        super(BotOwner.class);
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
