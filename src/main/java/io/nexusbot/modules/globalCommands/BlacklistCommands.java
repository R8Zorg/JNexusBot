package io.nexusbot.modules.globalCommands;

import java.awt.Color;
import java.util.List;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.OwnerOnly;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.nexusbot.database.entities.Blacklist;
import io.nexusbot.database.services.BlacklistService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class BlacklistCommands {
    private BlacklistService blacklistService = new BlacklistService();

    @Command(description = "Команды чёрного списка")
    @OwnerOnly(title = "Ошибка доступа", description = "Вы не можете использовать эту команду.", footer = "Эта группа команд доступна только владельцу бота.")
    @AdditionalSettings(defaultPermissions = Permission.ADMINISTRATOR)
    public void blacklist(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "blacklist", description = "Добавить в ЧС")
    public void add(SlashCommandInteractionEvent event,
            @Option(name = "user", description = "Участник или его ID") User user,
            @Option(name = "reason", description = "Причина") String reason) {
        try {
            blacklistService.add(user.getIdLong(), reason);
            EmbedUtil.replyEmbed(event, "Пользователь добавлен в чёрный список", Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event, "Не удалось сохранить пользователя в базу данных: " + e.getMessage(),
                    Color.RED);
        }
    }

    @Subcommand(parentNames = "blacklist", description = "Убрать из ЧС")
    public void remove(SlashCommandInteractionEvent event,
            @Option(name = "user", description = "Участник или его ID") User user) {
        try {
            blacklistService.remove(user.getIdLong());
            EmbedUtil.replyEmbed(event, "Пользователь удалён из чёрного списка", Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event, "Не удалось убрать пользователя из базы данных" + e.getMessage(),
                    Color.RED);
        }
    }

    @Subcommand(parentNames = "blacklist", description = "Посмотреть чёрный список")
    public void show(SlashCommandInteractionEvent event) {
        try {
            List<Blacklist> blacklists = blacklistService.getAll();
            if (blacklists.isEmpty()) {
                EmbedUtil.replyEmbed(event, "Список пуст", Color.WHITE);
                return;
            }

            String message = "`ID`: причина";
            for (Blacklist blacklist : blacklists) {
                message += "- `" + blacklist.getUserId() + "`: " + blacklist.getReason() + "\n";
            }
            message += "\nВсего пользователей: " + blacklists.size();
            EmbedUtil.replyEmbed(event, message, Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event, "Не удалось получить пользователей из базы данных" + e.getMessage(),
                    Color.RED);
        }
    }
}
