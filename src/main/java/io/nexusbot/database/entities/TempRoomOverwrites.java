package io.nexusbot.database.entities;

import java.util.Map;

import com.vladmihalcea.hibernate.type.json.JsonType;

import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class TempRoomOverwrites {
    @Id
    private long ownerId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> overwrites;

    public TempRoomOverwrites() {
    }

    public TempRoomOverwrites(long ownerId, Map<String, Object> channelSettings) {
        this.ownerId = ownerId;
        this.overwrites = channelSettings;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public Map<String, Object> getOverwrites() {
        return overwrites;
    }

    public void setOverwrites(Map<String, Object> channelSettings) {
        this.overwrites = channelSettings;
    }
}
