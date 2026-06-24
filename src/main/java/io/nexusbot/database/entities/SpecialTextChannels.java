package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity()
@Table
public class SpecialTextChannels {
    @Id
    private long guildId;

    private Long helloGoodbyeChannelId;
    private Long adminsChannelId;
    private Long moderatorsChannelId;
    private Long textLogChannelId;

    public SpecialTextChannels() {
    }

    public SpecialTextChannels(long guildId) {
        setGuildId(guildId);
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public Long getHelloGoodbyeChannelId() {
        return helloGoodbyeChannelId;
    }

    public void setHelloGoodbyeChannelId(Long helloGoodbyeChannelId) {
        this.helloGoodbyeChannelId = helloGoodbyeChannelId;
    }

    public Long getAdminsChannelId() {
        return adminsChannelId;
    }

    public void setAdminsChannelId(Long adminsChannelId) {
        this.adminsChannelId = adminsChannelId;
    }

    public Long getModeratorsChannelId() {
        return moderatorsChannelId;
    }

    public void setModeratorsChannelId(Long moderatorsChannelid) {
        this.moderatorsChannelId = moderatorsChannelid;
    }

    public Long getTextLogChannelId() {
        return textLogChannelId;
    }

    public void setTextLogChannelId(Long textLogChannelId) {
        this.textLogChannelId = textLogChannelId;
    }
}
