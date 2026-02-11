package io.nexusbot.modules.tickets;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class TicketCommands {

    @Command(description = "Команды для билетов")
    @AdditionalSettings(defaultPermissions = Permission.ADMINISTRATOR)
    public void ticket(SlashCommandInteractionEvent event) {
    }

    @SubcommandGroup(parentName = "ticket", description = "Команды для настройки билетов")
    public void set(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "ticket set")
    public void categories(SlashCommandInteractionEvent event,
            @Option(name = "current", description = "Категория для действующих билетов") Category currentCategory,
            @Option(name = "closed", description = "Категория для закрытых билетов") Category closedCategory) {
    }

    @Subcommand(parentNames = "ticket set")
    public void roles(SlashCommandInteractionEvent event) {
        // Roles?
    }

    @Subcommand(parentNames = "ticket", description = "Отправить начальное сообщение с кнопками")
    public void init(SlashCommandInteractionEvent event,
            @Option(name = "message", description = "Сообщение") String message,
            @Option(name = "url", description = "Ссылка на картинку под сообщением") String url) {
        // Color?
    }

}
