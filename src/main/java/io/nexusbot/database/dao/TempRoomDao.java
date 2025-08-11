package io.nexusbot.database.dao;

import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.interfaces.ITempRoom;

public class TempRoomDao implements ITempRoom {
    @Override
    public TempRoom get(long roomId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TempRoom.class, roomId);
        }
    }

    private <T> T extractField(long roomId, Function<TempRoom, T> extractor) {
        TempRoom tempRoom = get(roomId);
        return tempRoom == null ? null : extractor.apply(tempRoom);
    }

    @Override
    public Long getOwnerId(long roomId) {
        return extractField(roomId, TempRoom::getOwnerId);
    }

    @Override
    public void saveOrUpdate(TempRoom voiceChannel) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(voiceChannel);
            ta.commit();
        }
    }

    @Override
    public void remove(TempRoom voiceChannel) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.remove(voiceChannel);
            ta.commit();
        }
    }

    @Override
    public Long getCategoryId(long roomId) {
        return extractField(roomId, TempRoom::getCategoryId);
    }
}
