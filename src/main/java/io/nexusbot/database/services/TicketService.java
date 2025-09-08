package io.nexusbot.database.services;

import io.nexusbot.database.dao.TicketDao;
import io.nexusbot.database.entities.Ticket;
import io.nexusbot.database.interfaces.ITicket;

public class TicketService implements ITicket {
    private TicketDao ticketDao = new TicketDao();

    @Override
    public Ticket get(long guildId) {
        return ticketDao.get(guildId);
    }

    public Ticket getOrCreate(long guildId) {
        Ticket ticket = get(guildId);
        if (ticket == null) {
            ticket = new Ticket(guildId);
        }
        return ticket;
    }

    @Override
    public void saveOrUpdate(Ticket ticket) {
        ticketDao.saveOrUpdate(ticket);
    }

    @Override
    public void remove(Ticket ticket) {
        ticketDao.remove(ticket);
    }

}
