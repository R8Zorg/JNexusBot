package io.nexusbot.database.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.GuildInfo;
import io.nexusbot.database.interfaces.IGuildInfo;

public class GuildInfoDao implements IGuildInfo {
    public GuildInfo get(long guildId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(GuildInfo.class, guildId);
        }
    }

    public List<GuildInfo> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("From GuildInfo", GuildInfo.class).list();
        }
    }

    public void save(GuildInfo guild) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(guild);
            ta.commit();
        }
    }

    public void remove(GuildInfo guild) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.remove(guild);
            ta.commit();
        }
    }

}
