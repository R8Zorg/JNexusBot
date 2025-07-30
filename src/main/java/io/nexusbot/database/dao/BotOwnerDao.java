package io.nexusbot.database.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.BotOwner;
import io.nexusbot.database.interfaces.IBotOwner;

public class BotOwnerDao implements IBotOwner {
    public BotOwner get(long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(BotOwner.class, userId);
        }
    }

    public List<BotOwner> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("From BotOwner", BotOwner.class).list();
        }
    }

    public void add(BotOwner botOwner) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(botOwner);
            tx.commit();
        }
    }

    public void remove(BotOwner botOwner) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(botOwner);
            tx.commit();
        }
    }
}
