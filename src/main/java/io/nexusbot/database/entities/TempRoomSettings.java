package io.nexusbot.database.entities;

import java.util.ArrayList;
import java.util.List;

import com.vladmihalcea.hibernate.type.json.JsonType;

import org.hibernate.annotations.Type;

import io.nexusbot.componentsData.ChannelOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class TempRoomSettings {
    @EmbeddedId
    private TempRoomSettingsPK tempRoomSettingsPK;

    private String name;
    private int userLimit = 0;
    private boolean nsfw = false;
    private int bitrate = 64000;
    private String status;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<ChannelOverrides> overrides = new ArrayList<>();

    public TempRoomSettings() {
    }

    public TempRoomSettings(long ownerId, long guildId) {
        tempRoomSettingsPK = new TempRoomSettingsPK(ownerId, guildId);
    }

    public TempRoomSettingsPK getTempRoomSettingsPK() {
        return tempRoomSettingsPK;
    }

    public void setTempRoomSettingsPK(TempRoomSettingsPK tempRoomSettingsPK) {
        this.tempRoomSettingsPK = tempRoomSettingsPK;
    }

    public List<ChannelOverrides> getOverrides() {
        return overrides;
    }

    public void setOverrides(List<ChannelOverrides> overrides) {
        this.overrides = overrides;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }
}
