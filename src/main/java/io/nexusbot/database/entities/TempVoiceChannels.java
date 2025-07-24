package io.nexusbot.database.entities;

import java.util.Map;

import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.JsonJdbcType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class TempVoiceChannels {
    @Id
    private long channelId;

    private long guildId;
    private long ownerId;

    @JdbcType(JsonJdbcType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> channelSettings;
}
