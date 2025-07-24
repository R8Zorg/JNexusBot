package io.nexusbot.database.dao;

import java.util.List;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.GuildInfo;
import io.nexusbot.database.interfaces.IGuildEntityDao;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class GuildEntityDao implements IGuildEntityDao {
    @Override
    public GuildInfo get(long guildId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(GuildInfo.class, guildId);
        }
    }

    @Override
    public List<GuildInfo> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("From GuildEntity", GuildInfo.class).list();
        }
    }

    @Override
    public void save(GuildInfo guild) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(guild);
            ta.commit();
        }
    }

    @Override
    public void remove(GuildInfo guild) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.remove(guild);
            ta.commit();
        }
    }

}
