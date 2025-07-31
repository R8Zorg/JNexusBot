package io.nexusbot.database.services;

import java.util.Map;

import io.nexusbot.database.dao.TempVoiceChannelDao;
import io.nexusbot.database.entities.TempVoiceChannel;
import io.nexusbot.database.interfaces.ITempVoiceChannel;

public class TempVoiceChannelService implements ITempVoiceChannel {
    private final TempVoiceChannelDao voiceChannelDao = new TempVoiceChannelDao();
    @Override
    public TempVoiceChannel get(long voiceChannelId) {
        return voiceChannelDao.get(voiceChannelId);
    }

    @Override
    public long getOwnerId(long voiceChannelId) {
        return voiceChannelDao.getOwnerId(voiceChannelId);
    }

    @Override
    public Map<String, Object> getChannelSettings(long voiceChannelId) {
        return voiceChannelDao.getChannelSettings(voiceChannelId);
    }

    @Override
    public void saveOrUpdate(TempVoiceChannel voiceChannel) {
        voiceChannelDao.saveOrUpdate(voiceChannel);
    }

    @Override
    public void remove(TempVoiceChannel voiceChannel) {
        voiceChannelDao.remove(voiceChannel);
    }
}
