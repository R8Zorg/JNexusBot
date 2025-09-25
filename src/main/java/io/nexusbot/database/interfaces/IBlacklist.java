package io.nexusbot.database.interfaces;

import java.util.List;

import io.nexusbot.database.entities.Blacklist;

public interface IBlacklist {
    Blacklist get(long userId);
    List<Blacklist> getAll();
    void add(Blacklist blacklist);
    void remove(Blacklist blacklist);
}
