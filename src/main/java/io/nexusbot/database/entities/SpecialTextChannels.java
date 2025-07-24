package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "special_text_channels")
@Table
public class SpecialTextChannels {
    @Id
    private long guildId;

    private long helloGoodbyeChannelId;
    private long adminsChannelId;
    private long moderatorsChannelid;
}
