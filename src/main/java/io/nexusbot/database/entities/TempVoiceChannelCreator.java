package io.nexusbot.database.entities;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import io.nexusbot.database.enums.ChannelMode;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class TempVoiceChannelCreator {
    @Id
    private long id;

    @OneToOne
    @JoinColumn(name = "guild_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GuildInfo guild;

    private long tempVoiceChannelCategoryId;
    private int userLimit = 0;
    private String defaultTempChannelName;

    @Enumerated(EnumType.STRING)
    private ChannelMode channelMode = ChannelMode.BASIC;

    private boolean roleNeeded = false;
    private String roleNotFoundMessage;

    @ElementCollection
    @CollectionTable(name = "temp_voice_channel_creator_needed_roles_ids", joinColumns = @JoinColumn(name = "guild_id"))
    @Column(name = "role_id")
    private List<Long> neededRolesIds = new ArrayList<>();

    private Long logChannelId;

    public TempVoiceChannelCreator() {
    }

    public TempVoiceChannelCreator(long id, GuildInfo guild) {
        this.id = id;
        this.guild = guild;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GuildInfo getGuild() {
        return guild;
    }

    public void setGuild(GuildInfo guild) {
        this.guild = guild;
    }

    public long getTempVoiceChannelCategoryId() {
        return tempVoiceChannelCategoryId;
    }

    public void setTempVoiceChannelCategoryId(long tempVoiceChannelCategoryId) {
        this.tempVoiceChannelCategoryId = tempVoiceChannelCategoryId;
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

    public ChannelMode getChannelMode() {
        return channelMode;
    }

    public void setChannelMode(ChannelMode channelMode) {
        this.channelMode = channelMode;
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
}
