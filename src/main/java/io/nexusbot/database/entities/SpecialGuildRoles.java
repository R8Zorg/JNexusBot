package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class SpecialGuildRoles {
    @Id
    @ManyToOne()
    private GuildInfo guild;

    private long mutedRoleId;
}
