package io.nexusbot.database.services;

import io.nexusbot.database.dao.AbstractCrudDao;
import io.nexusbot.database.entities.Ticket;

public class TicketService extends AbstractCrudDao<Ticket, Long> {
    public TicketService() {
        super(Ticket.class);
    }

    public Ticket getOrCreate(long guildId) {
        Ticket ticket = get(guildId);
        if (ticket == null) {
            ticket = new Ticket(guildId);
        }
        return ticket;
    }
}
