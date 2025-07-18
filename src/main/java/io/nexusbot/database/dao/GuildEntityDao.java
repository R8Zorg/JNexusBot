package io.nexusbot.database.dao;

import java.util.List;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.GuildEntity;
import io.nexusbot.database.interfaces.IGuildEntityDao;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class GuildEntityDao implements IGuildEntityDao {
    @Override
    public GuildEntity get(long guildId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(GuildEntity.class, guildId);
        }
    }

    @Override
    public List<GuildEntity> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("From GuildEntity", GuildEntity.class).list();
        }
    }

    @Override
    public void save(GuildEntity guild) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(guild);
            ta.commit();
        }
    }

    @Override
    public void remove(GuildEntity guild) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.remove(guild);
            ta.commit();
        }
    }

}
