package io.nexusbot.database.services;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.dao.AbstractCrudDao;
import io.nexusbot.database.entities.TempRoomCreator;

public class TempRoomCreatorService extends AbstractCrudDao<TempRoomCreator, Long> {
    public TempRoomCreatorService() {
        super(TempRoomCreator.class);
    }

    private <T> T extractField(long roomCreatorId, Function<TempRoomCreator, T> extractor) {
        TempRoomCreator roomCreator = get(roomCreatorId);
        return roomCreator == null ? null : extractor.apply(roomCreator);
    }

    public Long getTempRoomCategoryId(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getTempRoomCategoryId);
    }

    public Integer getUserLimit(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getUserLimit);
    }

    public String getDefaultTempChannelName(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getDefaultTempChannelName);
    }

    public String getChannelMode(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getChannelMode);
    }

    public boolean isRoleNeeded(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::isRoleNeeded);
    }

    public String getRoleNotFoundMessage(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getRoleNotFoundMessage);
    }

    public List<Long> getNeededRolesIds(long roomCreatorId) {
        TempRoomCreator roomCreator = get(roomCreatorId);
        if (roomCreator == null) {
            return Collections.emptyList();
        }
        Hibernate.initialize(roomCreator.getNeededRolesIds());
        return roomCreator.getNeededRolesIds();
    }

    // TODO: нужен ли сеттер?
    public void setNeededRolesIds(long roomCreatorId, List<Long> rolesIds) {
        TempRoomCreator roomCreator = get(roomCreatorId);
        if (roomCreator == null) {
            throw new NullPointerException("RoomCreator doesn't exists in database");
        }
        roomCreator.setNeededRolesIds(rolesIds);
        saveOrUpdate(roomCreator);
    }

    public Long getLogChannelId(long roomCreatorId) {
        return extractField(roomCreatorId, TempRoomCreator::getLogChannelId);
    }

    public void saveOrUpdate(TempRoomCreator voiceChannelCreator) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(voiceChannelCreator);
            ta.commit();
        }
    }

    public TempRoomCreator getOrCreate(long roomCreatorId) {
        TempRoomCreator tempRoomCreator = get(roomCreatorId);
        if (tempRoomCreator == null) {
            tempRoomCreator = new TempRoomCreator(roomCreatorId);
        }
        return tempRoomCreator;
    }

}
