package io.nexusbot.database.interfaces;

import java.util.List;

import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.enums.ChannelMode;

public interface ITempRoomCreator {
    TempRoomCreator get(long voiceChannelId);
    void saveOrUpdate(TempRoomCreator voiceChannelCreator);
    void remove(TempRoomCreator voiceChannelCreator);
    Long getTempRoomCategoryId(long voiceChannelId);
    Integer getUserLimit(long voiceChannelId);
    String getDefaultTempChannelName(long voiceChannelId);
    ChannelMode getChannelMode(long voiceChannelId);
    boolean isRoleNeeded(long voiceChannelId);
    String getRoleNotFoundMessage(long voiceChannelId);
    List<Long> getNeededRolesIds(long voiceChannelId);
    Long getLogChannelId(long voiceChannelId);
}
