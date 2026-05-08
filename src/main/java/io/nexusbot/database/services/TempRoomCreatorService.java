package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.TempRoomCreator;

public class TempRoomCreatorService extends CrudDao<TempRoomCreator, Long> {
    public TempRoomCreatorService() {
        super(TempRoomCreator.class);
    }

    public TempRoomCreator getOrCreate(long roomCreatorId) {
        TempRoomCreator tempRoomCreator = get(roomCreatorId);
        if (tempRoomCreator == null) {
            tempRoomCreator = new TempRoomCreator(roomCreatorId);
        }
        return tempRoomCreator;
    }

}
