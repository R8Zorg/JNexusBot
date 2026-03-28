package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.TempRoom;

public class TempRoomService extends CrudDao<TempRoom, Long> {
    public TempRoomService() {
        super(TempRoom.class);
    }
}
