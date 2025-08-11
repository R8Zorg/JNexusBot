package io.nexusbot.database.services;

import java.util.List;

import io.nexusbot.database.dao.TempRoomCreatorDao;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.interfaces.ITempRoomCreator;

public class TempRoomCreatorService implements ITempRoomCreator {
    private final TempRoomCreatorDao voiceCreatorDao = new TempRoomCreatorDao();

    public TempRoomCreatorService() {
    }

    @Override
    public TempRoomCreator get(long roomCreatorId) {
        return voiceCreatorDao.get(roomCreatorId);
    }

    public TempRoomCreator getOrCreate(long roomCreatorId) {
        TempRoomCreator tempRoomCreator = get(roomCreatorId);
        if (tempRoomCreator == null) {
            tempRoomCreator = new TempRoomCreator(roomCreatorId);
        }
        return tempRoomCreator;
    }

    @Override
    public Long getTempRoomCategoryId(long roomCreatorId) {
        return voiceCreatorDao.getTempRoomCategoryId(roomCreatorId);
    }

    @Override
    public Integer getUserLimit(long roomCreatorId) {
        return voiceCreatorDao.getUserLimit(roomCreatorId);
    }

    @Override
    public String getDefaultTempChannelName(long roomCreatorId) {
        return voiceCreatorDao.getDefaultTempChannelName(roomCreatorId);
    }

    @Override
    public String getChannelMode(long roomCreatorId) {
        return voiceCreatorDao.getChannelMode(roomCreatorId);
    }

    @Override
    public boolean isRoleNeeded(long roomCreatorId) {
        return voiceCreatorDao.isRoleNeeded(roomCreatorId);
    }

    @Override
    public String getRoleNotFoundMessage(long roomCreatorId) {
        return voiceCreatorDao.getRoleNotFoundMessage(roomCreatorId);
    }

    @Override
    public List<Long> getNeededRolesIds(long roomCreatorId) {
        return voiceCreatorDao.getNeededRolesIds(roomCreatorId);
    }

    @Override
    public void setNeededRolesIds(long roomCreatorId, List<Long> rolesIds) {
        voiceCreatorDao.setNeededRolesIds(roomCreatorId, rolesIds);
    }

    @Override
    public Long getLogChannelId(long roomCreatorId) {
        return voiceCreatorDao.getLogChannelId(roomCreatorId);
    }

    @Override
    public void saveOrUpdate(TempRoomCreator roomCreator) {
        voiceCreatorDao.saveOrUpdate(roomCreator);
    }

    @Override
    public void remove(TempRoomCreator voiceChannelCreator) {
        voiceCreatorDao.remove(voiceChannelCreator);
    }
}
