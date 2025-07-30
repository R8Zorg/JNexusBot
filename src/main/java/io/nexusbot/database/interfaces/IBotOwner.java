package io.nexusbot.database.interfaces;

import java.util.List;

import io.nexusbot.database.entities.BotOwner;

public interface IBotOwner {
    BotOwner get(long userId);
    List<BotOwner> getAll();
    void add(BotOwner botOwner);
    void remove(BotOwner botOwner);
}
