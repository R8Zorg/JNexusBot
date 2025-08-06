package io.nexusbot.database.services;

import java.util.List;

import io.nexusbot.componentsData.PermissionOverwrite;
import io.nexusbot.database.dao.TempRoomOverwritesDao;
import io.nexusbot.database.entities.TempRoomOverwrites;
import io.nexusbot.database.interfaces.ITempRoomOverwrites;

public class TempRoomOverwritesService implements ITempRoomOverwrites {
    private final TempRoomOverwritesDao channelOverwritesDao = new TempRoomOverwritesDao();

    @Override
    public TempRoomOverwrites get(long ownerId, long guildId) {
        return channelOverwritesDao.get(ownerId, guildId);
    }

    public TempRoomOverwrites getOrCreate(long ownerId, long guildId) {
        TempRoomOverwrites roomOverwrites = get(ownerId, guildId);
        if (roomOverwrites == null) {
            roomOverwrites = new TempRoomOverwrites(ownerId, guildId);
        }
        return roomOverwrites;
    }

    public List<PermissionOverwrite> getOverwrites(long ownerId, long guildId) {
        TempRoomOverwrites overwrites = get(ownerId, guildId);
        return overwrites != null ? overwrites.getOverwrites() : null;
    }

    public void setOverwrites(long ownerId, long guildId, List<PermissionOverwrite> newOverwrites) {
        TempRoomOverwrites overwrites = get(ownerId, guildId);
        overwrites.setOverwrites(newOverwrites);
    }

    @Override
    public void saveOrUpdate(TempRoomOverwrites voiceChannelOverwrites) {
        channelOverwritesDao.saveOrUpdate(voiceChannelOverwrites);
    }
}
