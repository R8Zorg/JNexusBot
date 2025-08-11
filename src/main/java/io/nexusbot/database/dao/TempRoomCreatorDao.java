package io.nexusbot.database.dao;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.interfaces.ITempRoomCreator;

public class TempRoomCreatorDao implements ITempRoomCreator {
    @Override
    public TempRoomCreator get(long roomCreatorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TempRoomCreator.class, roomCreatorId);
        }
    }

    private <T> T extractField(long roomCreatorId, Function<TempRoomCreator, T> extractor) {
        TempRoomCreator roomCreator = get(roomCreatorId);
        return roomCreator == null ? null : extractor.apply(roomCreator);
    }

    @Override
    public Long getTempRoomCategoryId(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getTempRoomCategoryId);
    }

    @Override
    public Integer getUserLimit(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getUserLimit);
    }

    @Override
    public String getDefaultTempChannelName(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getDefaultTempChannelName);
    }

    @Override
    public String getChannelMode(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getChannelMode);
    }

    @Override
    public boolean isRoleNeeded(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::isRoleNeeded);
    }

    @Override
    public String getRoleNotFoundMessage(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getRoleNotFoundMessage);
    }

    @Override
    public List<Long> getNeededRolesIds(long roomCreatorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TempRoomCreator roomCreator = session.get(TempRoomCreator.class, roomCreatorId);
            if (roomCreator == null) {
                return Collections.emptyList();
            }
            Hibernate.initialize(roomCreator.getNeededRolesIds());
            return roomCreator.getNeededRolesIds();
        }
    }

    @Override
    public void setNeededRolesIds(long roomCreatorId, List<Long> rolesIds) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            TempRoomCreator roomCreator = session.get(TempRoomCreator.class, roomCreatorId);
            if (roomCreator != null) {
                roomCreator.setNeededRolesIds(rolesIds);
                session.merge(roomCreator);
            }
            ta.commit();
        }
    }

    @Override
    public Long getLogChannelId(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getLogChannelId);
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
