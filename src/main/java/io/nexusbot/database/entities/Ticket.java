package io.nexusbot.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Ticket {
    @Id
    private long guildId;

    private Integer buttonCooldown;
    private Long categoryId;
    private Long closedCategoryId;
    private Long logChannelId;
    private Integer number;

    public Ticket() {
    }

    public Ticket(long guildId) {
        this.guildId = guildId;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public Integer getButtonCooldown() {
        return buttonCooldown;
    }

    public void setButtonCooldown(Integer buttonCooldown) {
        this.buttonCooldown = buttonCooldown;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getClosedCategoryId() {
        return closedCategoryId;
    }

    public void setClosedCategoryId(Long closedCategoryId) {
        this.closedCategoryId = closedCategoryId;
    }

    public Long getLogChannelId() {
        return logChannelId;
    }

    public void setLogChannelId(Long logChannelId) {
        this.logChannelId = logChannelId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Ticket{guildId=" + guildId + "}";
    }
}
