package io.nexusbot.database.entities;

import java.util.ArrayList;
import java.util.List;

import io.nexusbot.componentsData.ChannelMode;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table
public class TempRoomCreator {
    @Id
    private long id;

    private long tempRoomCategoryId;
    private int userLimit = 0;
    private String defaultTempChannelName;

    private String channelMode = ChannelMode.basic;

    private boolean roleNeeded = false;
    private String roleNotFoundMessage;

    @ElementCollection
    @CollectionTable(name = "temp_voice_channel_creator_needed_roles_ids", joinColumns = @JoinColumn(name = "guild_id"))
    @Column(name = "role_id")
    private List<Long> neededRolesIds = new ArrayList<>();

    private Long logChannelId;

    public TempRoomCreator() {
    }

    public TempRoomCreator(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTempRoomCategoryId() {
        return tempRoomCategoryId;
    }

    public void setTempRoomCategoryId(long tempRoomCategoryId) {
        this.tempRoomCategoryId = tempRoomCategoryId;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }

    public String getDefaultTempChannelName() {
        return defaultTempChannelName;
    }

    public void setDefaultTempChannelName(String defaultTempChannelName) {
        this.defaultTempChannelName = defaultTempChannelName;
    }

    public boolean isRoleNeeded() {
        return roleNeeded;
    }

    public void setRoleNeeded(boolean isRoleNeeded) {
        this.roleNeeded = isRoleNeeded;
    }

    public String getRoleNotFoundMessage() {
        return roleNotFoundMessage;
    }

    public void setRoleNotFoundMessage(String roleNotFoundMessage) {
        this.roleNotFoundMessage = roleNotFoundMessage;
    }

    public List<Long> getNeededRolesIds() {
        return neededRolesIds;
    }

    public void setNeededRolesIds(List<Long> neededRolesIds) {
        this.neededRolesIds = neededRolesIds;
    }

    public Long getLogChannelId() {
        return logChannelId;
    }

    public void setLogChannelId(Long logChannelId) {
        this.logChannelId = logChannelId;
    }

    public String getChannelMode() {
        return channelMode;
    }

    public void setChannelMode(String channelMode) {
        this.channelMode = channelMode;
    }
}
