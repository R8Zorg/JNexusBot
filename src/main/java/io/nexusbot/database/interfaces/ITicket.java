package io.nexusbot.database.interfaces;

import io.nexusbot.database.entities.Ticket;

public interface ITicket {
    Ticket get(long guildId);
    void saveOrUpdate(Ticket ticket);
    void remove(Ticket ticket);
}
