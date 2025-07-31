package io.nexusbot.database.dao;

import java.util.Map;
import java.util.NoSuchElementException;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.TempVoiceChannel;
import io.nexusbot.database.interfaces.ITempVoiceChannel;

public class TempVoiceChannelDao implements ITempVoiceChannel {
    @Override
    public TempVoiceChannel get(long voiceChannelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TempVoiceChannel.class, voiceChannelId);
        }
    }

    @Override
    public long getOwnerId(long voiceChannelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM TempVoiceChannel t WHERE t.ownerId = :voiceChannelId", Long.class)
                    .setParameter("voiceChannelId", voiceChannelId)
                    .uniqueResult();

        }
    }

    @Override
    public Map<String, Object> getChannelSettings(long voiceChannelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TempVoiceChannel voiceChannel = session.get(TempVoiceChannel.class, voiceChannelId);
            if (voiceChannel == null) {
                throw new NoSuchElementException(
                        "TempVoiceChannelCreator not found for voiceChannelId: " + voiceChannelId);
            }
            return voiceChannel.getChannelSettings();
        }
    }

    @Override
    public void saveOrUpdate(TempVoiceChannel voiceChannel) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(voiceChannel);
            ta.commit();
        }
    }

    @Override
    public void remove(TempVoiceChannel voiceChannel) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.remove(voiceChannel);
            ta.commit();
        }
    }
}
