package io.nexusbot.database.services;

import java.util.Map;

import io.nexusbot.database.dao.TempRoomOverwritesDao;
import io.nexusbot.database.entities.TempRoomOverwrites;
import io.nexusbot.database.interfaces.ITempRoomOverwrites;

public class TempRoomOverwritesService implements ITempRoomOverwrites {
    private final TempRoomOverwritesDao channelOverwritesDao = new TempRoomOverwritesDao();

    @Override
    public Map<String, Object> getSettings(long ownerId) {
        return channelOverwritesDao.getSettings(ownerId);
    }

    @Override
    public void saveOrUpdate(TempRoomOverwrites voiceChannelOverwrites) {
        channelOverwritesDao.saveOrUpdate(voiceChannelOverwrites);
    }
    
}
