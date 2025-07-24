package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class TempVoiceChannelCreatorNeededRole {
    @Id
    private long channelCreatorId;

    @Id
    private long guildId;

    private long roleId;
}
