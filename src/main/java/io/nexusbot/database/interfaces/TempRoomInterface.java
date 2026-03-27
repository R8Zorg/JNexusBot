package io.nexusbot.database.interfaces;

public interface TempRoomInterface {
    Long getOwnerId(long roomId);

    Long getCategoryId(long roomId);
}
