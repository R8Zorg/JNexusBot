package io.nexusbot.database.entities;

import java.util.List;

import com.vladmihalcea.hibernate.type.json.JsonType;

import org.hibernate.annotations.Type;

import io.nexusbot.componentsData.PermissionOverwrite;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class TempRoomOverwrites {
    @EmbeddedId
    private TempRoomOverwritesPK tempRoomOverwritesPK;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<PermissionOverwrite> overwrites;

    public TempRoomOverwrites() {
    }

    public TempRoomOverwrites(long ownerId, long guildId) {
        tempRoomOverwritesPK = new TempRoomOverwritesPK(ownerId, guildId);
    }

    public TempRoomOverwritesPK getTempRoomOverwritesPK() {
        return tempRoomOverwritesPK;
    }

    public void setTempRoomOverwritesPK(TempRoomOverwritesPK tempRoomOverwritesPK) {
        this.tempRoomOverwritesPK = tempRoomOverwritesPK;
    }

    public List<PermissionOverwrite> getOverwrites() {
        return overwrites;
    }

    public void setOverwrites(List<PermissionOverwrite> overwrites) {
        this.overwrites = overwrites;
    }
}
