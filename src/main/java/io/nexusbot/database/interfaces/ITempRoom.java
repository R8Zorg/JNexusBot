package io.nexusbot.database.interfaces;

public interface ITempRoom {
    Long getOwnerId(long roomId);

    Long getCategoryId(long roomId);
}
