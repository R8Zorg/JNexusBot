package io.nexusbot.modules.commands;

import java.util.List;

import io.nexusbot.database.entities.GuildEntity;
import io.nexusbot.database.services.GuildEntityService;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class GuildManagmentCommands {

    GuildEntityService guildService = new GuildEntityService();

    @Command
    public void guilds(SlashCommandInteractionEvent event) {
    }

    @SubcommandGroup(parentName = "guilds")
    public void get(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "guilds get")
    public void all(SlashCommandInteractionEvent event) {
        List<GuildEntity> guildEntities = guildService.getAll();
        if (guildEntities.isEmpty()) {
            event.reply("Gulds not found").setEphemeral(true).queue();
            return;
        }
        String replyMessage = "";
        JDA jda = event.getJDA();
        for (GuildEntity guildEntity : guildEntities) {
            Guild guild = jda.getGuildById(guildEntity.getId());
            replyMessage += "- " + guild.getName() + " [`" + guildEntity.getId() + "`]";
        }
        event.reply(replyMessage).queue();
    }

    @Subcommand(parentNames = "guilds")
    public void add(SlashCommandInteractionEvent event,
            @Option(name = "guild_id", description = "Specific guild") String guildId) {
        // TODO: combine this
        Guild providedGuild;
        try {
            providedGuild = event.getJDA().getGuildById(guildId);
        } catch (NumberFormatException e) {
            event.reply("Introduced invalid guild id").setEphemeral(true).queue();
            return;
        }
        GuildEntity guild = new GuildEntity(providedGuild.getIdLong(), providedGuild.getOwnerIdLong());
        guildService.save(guild);
        event.reply("Added").setEphemeral(true).queue();
    }

    @Subcommand(parentNames = "guilds")
    public void remove(SlashCommandInteractionEvent event,
            @Option(name = "guild_id", description = "Specific guild") String guildId) {
        // TODO: with this
        Guild providedGuild;
        try {
            providedGuild = event.getJDA().getGuildById(guildId);
        } catch (NumberFormatException e) {
            event.reply("Introduced invalid guild id").setEphemeral(true).queue();
            return;
        }
        try {
            GuildEntity guild = guildService.get(guildId);
            guildService.remove(guild);
            event.reply("Guild (" + providedGuild.getName() + ") [" + providedGuild.getId() + "] removed")
                    .setEphemeral(true).queue();
        } catch (IllegalArgumentException e) {
            event.reply("No such guild in database").setEphemeral(true).queue();
        }
    }
}
