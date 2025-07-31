package io.nexusbot.database.interfaces;

import java.util.List;

import io.nexusbot.database.entities.TempVoiceChannelCreator;
import io.nexusbot.database.enums.ChannelMode;

public interface ITempVoiceChannelCreator {
    TempVoiceChannelCreator get(long voiceChannelId);
    void saveOrUpdate(TempVoiceChannelCreator voiceChannelCreator);
    void remove(TempVoiceChannelCreator voiceChannelCreator);
    long getTempVoiceChannelCategoryId(long voiceChannelId);
    int getUserLimit(long voiceChannelId);
    String getDefaultTempChannelName(long voiceChannelId);
    ChannelMode getChannelMode(long voiceChannelId);
    boolean getRoleNeeded(long voiceChannelId);
    String getRoleNotFoundMessage(long voiceChannelId);
    List<Long> getNeededRolesIds(long voiceChannelId);
    long getLogChannelId(long voiceChannelId);
}
