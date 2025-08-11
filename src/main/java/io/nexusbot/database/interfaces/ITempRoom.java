package io.nexusbot.database.interfaces;

import io.nexusbot.database.entities.TempRoom;

public interface ITempRoom {
    TempRoom get(long roomId);
    void saveOrUpdate(TempRoom voiceChannel);
    void remove(TempRoom voiceChannel);
    Long getOwnerId(long roomId);
    Long getCategoryId(long roomId);
}
