package io.nexusbot.database.services;

import java.util.Map;

import io.nexusbot.database.dao.TempVoiceChannelDao;
import io.nexusbot.database.entities.TempVoiceChannel;
import io.nexusbot.database.interfaces.ITempVoiceChannel;

public class TempVoiceChannelService implements ITempVoiceChannel {
    private final TempVoiceChannelDao voiceChannel = new TempVoiceChannelDao();
    @Override
    public TempVoiceChannel get(long voiceChannelId) {
        return voiceChannel.get(voiceChannelId);
    }

    @Override
    public long getOwnerId(long voiceChannelId) {
        return voiceChannel.getOwnerId(voiceChannelId);
    }

    @Override
    public Map<String, Object> getChannelSettings(long voiceChannelId) {
        return voiceChannel.getChannelSettings(voiceChannelId);
    }

}
