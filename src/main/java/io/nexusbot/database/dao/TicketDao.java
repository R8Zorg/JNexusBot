package io.nexusbot.database.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.Ticket;
import io.nexusbot.database.interfaces.ITicket;

public class TicketDao implements ITicket {

    @Override
    public Ticket get(long guildId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Ticket.class, guildId);
        }
    }

    @Override
    public void saveOrUpdate(Ticket ticket) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(ticket);
            ta.commit();
        }
    }

    @Override
    public void remove(Ticket ticket) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.remove(ticket);
            ta.commit();
        }
    }

}
