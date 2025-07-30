package io.nexusbot.database.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity(name = "special_text_channels")
@Table
public class SpecialTextChannel {
    @Id
    private long guildId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "guild_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GuildInfo guild;

    private Long helloGoodbyeChannelId;
    private Long adminsChannelId;
    private Long moderatorsChannelid;

    public SpecialTextChannel(GuildInfo guild) {
        this.guild = guild;
    }

    public SpecialTextChannel() {
    }

    public GuildInfo getGuild() {
        return guild;
    }

    public void setGuild(GuildInfo guild) {
        this.guild = guild;
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

    public Long getModeratorsChannelid() {
        return moderatorsChannelid;
    }

    public void setModeratorsChannelid(Long moderatorsChannelid) {
        this.moderatorsChannelid = moderatorsChannelid;
    }
}
