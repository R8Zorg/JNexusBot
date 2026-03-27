package io.nexusbot.database.services;

import java.util.function.Function;

import io.nexusbot.database.dao.AbstractCrudDao;
import io.nexusbot.database.entities.TempRoom;

public class TempRoomService extends AbstractCrudDao<TempRoom, Long> {
    public TempRoomService() {
        super(TempRoom.class);
    }

    private <T> T extractField(long roomId, Function<TempRoom, T> extractor) {
        TempRoom tempRoom = get(roomId);
        return tempRoom == null ? null : extractor.apply(tempRoom);
    }

    public Long getOwnerId(long roomId) {
        return extractField(roomId, TempRoom::getOwnerId);
    }

    public Long getCategoryId(long roomId) {
        return extractField(roomId, TempRoom::getCategoryId);
    }
}
