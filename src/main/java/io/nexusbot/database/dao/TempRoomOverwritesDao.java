package io.nexusbot.database.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.TempRoomOverwrites;
import io.nexusbot.database.entities.TempRoomOverwritesPK;
import io.nexusbot.database.interfaces.ITempRoomOverwrites;

public class TempRoomOverwritesDao implements ITempRoomOverwrites {
    @Override
    public TempRoomOverwrites get(long ownerId, long guildId) {
        TempRoomOverwritesPK pk = new TempRoomOverwritesPK(ownerId, guildId);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TempRoomOverwrites.class, pk);
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
