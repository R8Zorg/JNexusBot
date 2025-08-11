package io.nexusbot.database.interfaces;

import java.util.List;

import io.nexusbot.database.entities.TempRoomCreator;

public interface ITempRoomCreator {
    TempRoomCreator get(long roomCreatorId);
    void saveOrUpdate(TempRoomCreator roomCreator);
    void remove(TempRoomCreator roomCreator);
    Long getTempRoomCategoryId(long roomCreatorId);
    Integer getUserLimit(long roomCreatorId);
    String getDefaultTempChannelName(long roomCreatorId);
    String getChannelMode(long roomCreatorId);
    boolean isRoleNeeded(long roomCreatorId);
    String getRoleNotFoundMessage(long roomCreatorId);
    List<Long> getNeededRolesIds(long roomCreatorId);
    void setNeededRolesIds(long roomCreatorId, List<Long> rolesIds);
    Long getLogChannelId(long roomCreatorId);
}
