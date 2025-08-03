package io.nexusbot.database.services;

import io.nexusbot.database.dao.TempRoomDao;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.interfaces.ITempRoom;

public class TempRoomService implements ITempRoom {
    private final TempRoomDao voiceChannelDao = new TempRoomDao();

    @Override
    public TempRoom get(long voiceChannelId) {
        return voiceChannelDao.get(voiceChannelId);
    }

    public TempRoom getOrCreate(long voiceChannelId) {
        TempRoom tempRoom = get(voiceChannelId);
        if (tempRoom == null) {
            tempRoom = new TempRoom(voiceChannelId);
        }
        return tempRoom;
    }

    @Override
    public Long getOwnerId(long voiceChannelId) {
        return voiceChannelDao.getOwnerId(voiceChannelId);
    }

    @Override
    public void saveOrUpdate(TempRoom voiceChannel) {
        voiceChannelDao.saveOrUpdate(voiceChannel);
    }

    @Override
    public void remove(TempRoom voiceChannel) {
        voiceChannelDao.remove(voiceChannel);
    }

    @Override
    public Long getCategoryId(long voiceChannelId) {
        return voiceChannelDao.getCategoryId(voiceChannelId);
    }
}
