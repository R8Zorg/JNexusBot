package io.nexusbot.database.dao;

import java.util.List;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.interfaces.ITempRoomCreator;

public class TempRoomCreatorDao implements ITempRoomCreator {
    @Override
    public TempRoomCreator get(long voiceChannelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TempRoomCreator.class, voiceChannelId);
        }
    }

    private <T> T extractField(long voiceChannelId, Function<TempRoomCreator, T> extractor) {
        TempRoomCreator roomCreator = get(voiceChannelId);
        return roomCreator == null ? null : extractor.apply(roomCreator);
    }

    @Override
    public Long getTempRoomCategoryId(long voiceChannelId) {
        return extractField(voiceChannelId, TempRoomCreator::getTempRoomCategoryId);
    }

    @Override
    public Integer getUserLimit(long voiceChannelId) {
        return extractField(voiceChannelId, TempRoomCreator::getUserLimit);
    }

    @Override
    public String getDefaultTempChannelName(long voiceChannelId) {
        return extractField(voiceChannelId, TempRoomCreator::getDefaultTempChannelName);
    }

    @Override
    public String getChannelMode(long voiceChannelId) {
        return extractField(voiceChannelId, TempRoomCreator::getChannelMode);
    }

    @Override
    public boolean isRoleNeeded(long voiceChannelId) {
        return extractField(voiceChannelId, TempRoomCreator::isRoleNeeded);
    }

    @Override
    public String getRoleNotFoundMessage(long voiceChannelId) {
        return extractField(voiceChannelId, TempRoomCreator::getRoleNotFoundMessage);
    }

    @Override
    public List<Long> getNeededRolesIds(long voiceChannelId) {
        return extractField(voiceChannelId, TempRoomCreator::getNeededRolesIds);
    }

    @Override
    public Long getLogChannelId(long voiceChannelId) {
        return extractField(voiceChannelId, TempRoomCreator::getLogChannelId);
    }

    @Override
    public void saveOrUpdate(TempRoomCreator voiceChannelCreator) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(voiceChannelCreator);
            ta.commit();
        }
    }

    @Override
    public void remove(TempRoomCreator voiceChannelCreator) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.remove(voiceChannelCreator);
            ta.commit();
        }
    }
}
