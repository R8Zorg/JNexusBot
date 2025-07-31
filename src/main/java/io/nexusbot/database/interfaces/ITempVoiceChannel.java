package io.nexusbot.database.interfaces;

import java.util.Map;

import io.nexusbot.database.entities.TempVoiceChannel;

public interface ITempVoiceChannel {
    TempVoiceChannel get(long voiceChannelId);
    void saveOrUpdate(TempVoiceChannel voiceChannel);
    void remove(TempVoiceChannel voiceChannel);
    long getOwnerId(long voiceChannelId);
    Map<String, Object> getChannelSettings(long voiceChannelId);
}
