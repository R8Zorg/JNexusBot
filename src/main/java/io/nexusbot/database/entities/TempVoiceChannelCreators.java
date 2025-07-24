package io.nexusbot.database.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class TempVoiceChannelCreators {
    @Id
    private long channelId;

    private long guildId;
    private long tempVoiceChannelCategoryId;
    private boolean isCustom = false;
    private int userLimit = 0;
    private boolean isRoleNeeded = false;
    private String roleNotFoundMessage;
    private List<Long> neededRolesIds;
    private boolean isLogNeeded = false;
    private String defaultTempChannelName;
}
