package io.nexusbot.database.services;

import org.hibernate.Hibernate;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.TempRoomCreator;

public class TempRoomCreatorService extends CrudDao<TempRoomCreator, Long> {
    public TempRoomCreatorService() {
        super(TempRoomCreator.class);
    }

    @Override
    public TempRoomCreator get(Long channelCreatorId) {
        TempRoomCreator tempRoomCreator = super.get(channelCreatorId);
        if (tempRoomCreator != null) {
            Hibernate.initialize(tempRoomCreator.getNeededRolesIds());
        }
        return tempRoomCreator;
    }

    public TempRoomCreator getOrCreate(long roomCreatorId) {
        TempRoomCreator tempRoomCreator = get(roomCreatorId);
        if (tempRoomCreator == null) {
            tempRoomCreator = new TempRoomCreator(roomCreatorId);
        }
        return tempRoomCreator;
    }

}
