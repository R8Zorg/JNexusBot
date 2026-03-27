package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.Ticket;

public class TicketService extends CrudDao<Ticket, Long> {
    public TicketService() {
        super(Ticket.class);
    }
}
