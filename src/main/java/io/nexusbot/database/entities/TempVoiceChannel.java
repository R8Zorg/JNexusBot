package io.nexusbot.database.entities;

import java.util.Map;

import com.vladmihalcea.hibernate.type.json.JsonType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class TempVoiceChannel {
    @Id
    private long id;

    @OneToOne
    @JoinColumn(name = "guild_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GuildInfo guild;

    private long ownerId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> channelSettings;

    public TempVoiceChannel(long id, GuildInfo guild) {
        this.id = id;
        this.guild = guild;
    }

    public TempVoiceChannel() {
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

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public Map<String, Object> getChannelSettings() {
        return channelSettings;
    }

    public void setChannelSettings(Map<String, Object> channelSettings) {
        this.channelSettings = channelSettings;
    }
}
