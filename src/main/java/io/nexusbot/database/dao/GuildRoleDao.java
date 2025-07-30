package io.nexusbot.database.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.GuildRole;
import io.nexusbot.database.interfaces.IGuildRole;

public class GuildRoleDao implements IGuildRole {
    @Override
    public GuildRole get(long roleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(GuildRole.class, roleId);
        }
    }

    @Override
    public List<GuildRole> getAll(long guildId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("From GuildRole", GuildRole.class).list();
        }
    }

    @Override
    public void add(GuildRole guildRole) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(guildRole);
            ta.commit();
        }
    }

    @Override
    public void remove(GuildRole guildRole) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.remove(guildRole);
            ta.commit();
        }
    }
}
