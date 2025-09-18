package io.nexusbot.database.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.Blacklist;
import io.nexusbot.database.interfaces.IBlacklist;

public class BlacklistDao implements IBlacklist {
    @Override
    public Blacklist get(long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Blacklist.class, userId);
        }
    }

    @Override
    public List<Blacklist> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("From Blacklist", Blacklist.class).list();
        }
    }

    @Override
    public void add(Blacklist blacklist) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(blacklist);
            tx.commit();
        }
    }

    @Override
    public void remove(Blacklist blacklist) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(blacklist);
            tx.commit();
        }
    }
    
}
