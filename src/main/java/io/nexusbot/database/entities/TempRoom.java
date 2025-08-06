package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class TempRoom {
    @Id
    private long id;

    private long ownerId;
    private long categoryId;
    private Long channelLogId;
    private Long logMessageId;

    public TempRoom(long id, long ownerId, long categoryId) {
        this.id = id;
        this.ownerId = ownerId;
        this.categoryId = categoryId;
    }

    public TempRoom() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getChannelLogId() {
        return channelLogId;
    }

    public void setChannelLogId(long channelCreatorId) {
        this.channelLogId = channelCreatorId;
    }

    public Long getLogMessageId() {
        return logMessageId;
    }

    public void setLogMessageId(Long logMessageId) {
        this.logMessageId = logMessageId;
    }
}
