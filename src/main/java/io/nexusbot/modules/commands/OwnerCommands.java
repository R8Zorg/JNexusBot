package io.nexusbot.modules.commands;

import java.awt.Color;
import java.util.List;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.OwnerOnly;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.nexusbot.database.entities.BotOwner;
import io.nexusbot.database.services.BotOwnerService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class OwnerCommands {
    BotOwnerService botOwnerService = new BotOwnerService();

    @Command(description = "Owner commands")
    @OwnerOnly(title = "Ошибка доступа", description = "Вы не можете использовать эту команду.", footer = "Эта группа команд доступна только владельцу бота.")
    @AdditionalSettings(defaultPermissions = Permission.ADMINISTRATOR)
    public void owners(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "owners", description = "Получить список владельцев бота")
    public void list(SlashCommandInteractionEvent event) {
        try {
            List<BotOwner> owners = botOwnerService.getAll();
            if (owners.isEmpty()) {
                EmbedUtil.replyEmbed(event, "Владельцев нет.", Color.YELLOW);
                return;
            }
            String replyMessage = "Владельцы бота:";
            for (BotOwner botOwner : owners) {
                replyMessage += "<@" + botOwner.getId() + ">\n";
            }
            EmbedUtil.replyEmbed(event, replyMessage, Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event, "Произошла ошибка: " + e.getMessage(), Color.RED);
        }
    }

    @Subcommand(parentNames = "owners", description = "Добавить пользователя в список создателей бота")
    public void add(SlashCommandInteractionEvent event,
            @Option(name = "user", description = "Пользователь") User user) {
        try {
            botOwnerService.add(user.getIdLong());
            EmbedUtil.replyEmbed(event, user.getAsMention() + " добавлен.", Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event, "Произошла ошибка: " + e.getMessage(), Color.RED);
        }
    }

    @Subcommand(parentNames = "owners", description = "Убрать пользователя из списка владельцев")
    public void remove(SlashCommandInteractionEvent event,
            @Option(name = "user", description = "Пользователь") User user) {
        if (user.getIdLong() == event.getUser().getIdLong()) {
            EmbedUtil.replyEmbed(event, "Вы не можете убрать себя из списка владельцев.", Color.RED);
            return;
        }
        try {
            botOwnerService.remove(user.getIdLong());
            EmbedUtil.replyEmbed(event, user.getAsMention() + " убран из списка владельцев.", Color.GREEN);
        } catch (IllegalArgumentException e) {
            EmbedUtil.replyEmbed(event, "Пользователь и так не владелец бота",
                    Color.RED);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event,
                    "Произошла ошибка: " + e.getMessage(), Color.RED);
        }
    }
}
