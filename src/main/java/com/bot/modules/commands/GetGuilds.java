package com.bot.modules.commands;

import java.util.List;

import com.bot.database.entities.GuildEntity;
import com.bot.database.services.GuildEntityService;
import com.bot.core.annotations.Command;
import com.bot.core.annotations.SlashCommands;
import com.bot.core.annotations.Subcommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class GetGuilds {
    @Command
    public void get(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "get")
    public void guilds(SlashCommandInteractionEvent event) {
        GuildEntityService guildService = new GuildEntityService();
        List<GuildEntity> guildEntities = guildService.getAllGuilds();
        String replyMessage = "Guilds not found";
        JDA jda = event.getJDA();
        for (GuildEntity guildEntity : guildEntities) {
            Guild guild = jda.getGuildById(guildEntity.getId());
            replyMessage += guild.getName() + " [" + guildEntity.getId() + "]";
        }
        event.reply(replyMessage).queue();
    }
}
