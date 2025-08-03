package io.nexusbot.database.services;

import java.util.List;

import io.nexusbot.database.dao.TempRoomCreatorDao;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.enums.ChannelMode;
import io.nexusbot.database.interfaces.ITempRoomCreator;

public class TempRoomCreatorService implements ITempRoomCreator {
    private final TempRoomCreatorDao voiceCreatorDao = new TempRoomCreatorDao();

    public TempRoomCreatorService() {
    }

    @Override
    public TempRoomCreator get(long voiceChannelId) {
        return voiceCreatorDao.get(voiceChannelId);
    }

    public TempRoomCreator getOrCreate(long voiceChannelId) {
        TempRoomCreator tempRoomCreator = get(voiceChannelId);
        if (tempRoomCreator == null) {
            tempRoomCreator = new TempRoomCreator(voiceChannelId);
        }
        return tempRoomCreator;
    }

    @Override
    public Long getTempRoomCategoryId(long voiceChannelId) {
        return voiceCreatorDao.getTempRoomCategoryId(voiceChannelId);
    }

    @Override
    public Integer getUserLimit(long voiceChannelId) {
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
    public boolean isRoleNeeded(long voiceChannelId) {
        return voiceCreatorDao.isRoleNeeded(voiceChannelId);
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
    public Long getLogChannelId(long voiceChannelId) {
        return voiceCreatorDao.getLogChannelId(voiceChannelId);
    }

    @Override
    public void saveOrUpdate(TempRoomCreator voiceChannelCreator) {
        voiceCreatorDao.saveOrUpdate(voiceChannelCreator);
    }

    @Override
    public void remove(TempRoomCreator voiceChannelCreator) {
        voiceCreatorDao.remove(voiceChannelCreator);
    }
}
