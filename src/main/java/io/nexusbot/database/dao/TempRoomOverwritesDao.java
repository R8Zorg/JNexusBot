package io.nexusbot.database.dao;

import java.util.Map;
import java.util.NoSuchElementException;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.TempRoomOverwrites;
import io.nexusbot.database.interfaces.ITempRoomOverwrites;

public class TempRoomOverwritesDao implements ITempRoomOverwrites {

    @Override
    public Map<String, Object> getSettings(long ownerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TempRoomOverwrites channelOverwrites = session.get(TempRoomOverwrites.class, ownerId);
            if (channelOverwrites == null) {
                throw new NoSuchElementException("tempRoomOverwrites not found for ownerId:" + ownerId);
            }
            return channelOverwrites.getOverwrites();
        }
    }

    @Override
    public void saveOrUpdate(TempRoomOverwrites voiceChannelOverwrites) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(voiceChannelOverwrites);
            ta.commit();
        }
    }

}
