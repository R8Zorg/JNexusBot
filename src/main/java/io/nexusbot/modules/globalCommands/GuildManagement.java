package io.nexusbot.modules.globalCommands;

import java.awt.Color;
import java.util.List;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.OwnerOnly;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import io.nexusbot.database.entities.GuildInfo;
import io.nexusbot.database.services.GuildInfoService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class GuildManagement {
    private GuildInfoService guildService = new GuildInfoService();

    @Command(description = "Команды для обновления серверов в БД. Доступны только создателю бота")
    @OwnerOnly(title = "Ошибка доступа", description = "Вы не можете использовать эту команду.", footer = "Эта группа команд доступна только владельцу бота.")
    @AdditionalSettings(defaultPermissions = Permission.ADMINISTRATOR)
    public void guilds(SlashCommandInteractionEvent event) {
    }

    @SubcommandGroup(parentName = "guilds")
    public void get(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "guilds get")
    public void all(SlashCommandInteractionEvent event) {
        List<GuildInfo> guildEntities = guildService.getAll();
        if (guildEntities.isEmpty()) {
            EmbedUtil.replyEmbed(event, "Серверы не найдены.", Color.RED);
            return;
        }
        String replyMessage = "";
        JDA jda = event.getJDA();
        for (GuildInfo guildEntity : guildEntities) {
            Guild guild = jda.getGuildById(guildEntity.getId());
            replyMessage += "- " + guild.getName() + " [`" + guildEntity.getId() + "`]";
        }
        EmbedUtil.replyEmbed(event, replyMessage, Color.GREEN);
    }

    private Guild getGuildOrRepyError(SlashCommandInteractionEvent event, String guildId) {
        try {
            Guild guild = event.getJDA().getGuildById(guildId);
            if (guild == null) {
                EmbedUtil.replyEmbed(event, "Сервер с таким ID не найден", Color.RED);
                return null;
            }
            return guild;
        } catch (NumberFormatException e) {
            EmbedUtil.replyEmbed(event, "Введён неверный ID сервера", Color.RED);
            return null;
        }
    }

    @Subcommand(parentNames = "guilds")
    public void add(SlashCommandInteractionEvent event,
            @Option(name = "guild_id", description = "ID сервера") String guildId) {
        Guild providedGuild = getGuildOrRepyError(event, guildId);
        GuildInfo guild = new GuildInfo(providedGuild.getIdLong(), providedGuild.getOwnerIdLong());
        try {
            guildService.saveOrUpdate(guild);
            EmbedUtil.replyEmbed(event, "Сервер добавлен", Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event, "Произошла ошибка: " + e.getMessage(), Color.RED);
        }
    }

    @Subcommand(parentNames = "guilds")
    public void remove(SlashCommandInteractionEvent event,
            @Option(name = "guild_id", description = "ID сервера") String guildId) {
        Guild providedGuild = getGuildOrRepyError(event, guildId);
        try {
            GuildInfo guild = guildService.get(Long.parseLong(guildId));
            guildService.remove(guild);
            EmbedUtil.replyEmbed(event,
                    "Сервер (" + providedGuild.getName() + ") [" + providedGuild.getId() + "] убран", Color.GREEN);
        } catch (IllegalArgumentException e) {
            EmbedUtil.replyEmbed(event, "Сервер не найден в базе данных", Color.RED);
        }
    }
}
