package io.nexusbot.database.services;

import java.util.List;

import org.hibernate.ObjectNotFoundException;

import io.nexusbot.database.dao.TempVoiceChannelCreatorDao;
import io.nexusbot.database.entities.TempVoiceChannelCreator;
import io.nexusbot.database.enums.ChannelMode;
import io.nexusbot.database.interfaces.ITempVoiceChannelCreator;

public class TempVoiceChannelCreatorService implements ITempVoiceChannelCreator {
    private final TempVoiceChannelCreatorDao voiceCreatorDao = new TempVoiceChannelCreatorDao();

    public TempVoiceChannelCreatorService() {
    }

    @Override
    public TempVoiceChannelCreator get(long voiceChannelId) throws ObjectNotFoundException {
        return voiceCreatorDao.get(voiceChannelId);
    }

    @Override
    public long getTempVoiceChannelCategoryId(long voiceChannelId) {
        return voiceCreatorDao.getTempVoiceChannelCategoryId(voiceChannelId);
    }

    @Override
    public int getUserLimit(long voiceChannelId) {
        return voiceCreatorDao.getUserLimit(voiceChannelId);
    }

    @Override
    public String getDefaultTempChannelName(long voiceChannelId) {
        return voiceCreatorDao.getDefaultTempChannelName(voiceChannelId);
    }

    @Override
    public ChannelMode getChannelMode(long voiceChannelId) {
        return voiceCreatorDao.getChannelMode(voiceChannelId);
    }

    @Override
    public boolean getRoleNeeded(long voiceChannelId) {
        return voiceCreatorDao.getRoleNeeded(voiceChannelId);
    }

    @Override
    public String getRoleNotFoundMessage(long voiceChannelId) {
        return voiceCreatorDao.getRoleNotFoundMessage(voiceChannelId);
    }

    @Override
    public List<Long> getNeededRolesIds(long voiceChannelId) {
        return voiceCreatorDao.getNeededRolesIds(voiceChannelId);
    }

    @Override
    public long getLogChannelId(long voiceChannelId) {
        return voiceCreatorDao.getLogChannelId(voiceChannelId);
    }
}
