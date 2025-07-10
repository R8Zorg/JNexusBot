package com.bot.database.dao;

import java.util.List;

import com.bot.database.HibernateUtil;
import com.bot.database.entities.GuildEntity;
import com.bot.database.interfaces.IGuildEntityDao;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class GuildEntityDao implements IGuildEntityDao {

    @Override
    public GuildEntity getOwnerId(long guildId) {
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
            session.persist(guild);
            ta.commit();
        }
    }

    @Override
    public void update(GuildEntity guild) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(guild);
            ta.commit();
        }
    }

    @Override
    public void delete(GuildEntity guild) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.remove(guild);
            ta.commit();
        }
    }

}
