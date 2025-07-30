package io.nexusbot.database.interfaces;

import java.util.Map;

import io.nexusbot.database.entities.TempVoiceChannel;

public interface ITempVoiceChannel {
    TempVoiceChannel get(long voiceChannelId);
    long getOwnerId(long voiceChannelId);
    Map<String, Object> getChannelSettings(long voiceChannelId);
}
