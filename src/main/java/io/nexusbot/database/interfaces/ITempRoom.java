package io.nexusbot.database.interfaces;

import io.nexusbot.database.entities.TempRoom;

public interface ITempRoom {
    TempRoom get(long voiceChannelId);
    void saveOrUpdate(TempRoom voiceChannel);
    void remove(TempRoom voiceChannel);
    Long getOwnerId(long voiceChannelId);
    Long getCategoryId(long voiceChannelId);
}
