package io.nexusbot.database.services;

import io.nexusbot.database.dao.TempRoomDao;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.interfaces.ITempRoom;

public class TempRoomService implements ITempRoom {
    private final TempRoomDao voiceChannelDao = new TempRoomDao();

    @Override
    public TempRoom get(long roomId) {
        return voiceChannelDao.get(roomId);
    }

    public TempRoom getOrCreate(long roomId, long ownerId, long categoryId) {
        TempRoom tempRoom = get(roomId);
        if (tempRoom == null) {
            tempRoom = new TempRoom(roomId, ownerId, categoryId);
        }
        return tempRoom != null ? tempRoom : null;
    }

    @Override
    public Long getOwnerId(long roomId) {
        return voiceChannelDao.getOwnerId(roomId);
    }

    @Override
    public void saveOrUpdate(TempRoom tempRoom) {
        voiceChannelDao.saveOrUpdate(tempRoom);
    }

    @Override
    public void remove(TempRoom voiceChannel) {
        voiceChannelDao.remove(voiceChannel);
    }

    @Override
    public Long getCategoryId(long roomId) {
        return voiceChannelDao.getCategoryId(roomId);
    }
}
