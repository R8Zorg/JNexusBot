package io.nexusbot.modules.setup;

import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.componentsData.MainMenu;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

@SlashCommands
public class SetupMenu {
    @Subcommand(parentNames = "setup", description = "Меню с информацией о настройке бота")
    public void menu(SlashCommandInteractionEvent event) {
        event.reply("")
                .addActionRow(
                        StringSelectMenu.create(MainMenu.id)
                                .addOption("Ничего", GlobalIds.NOTHING.getValue(), "Ничего не выбирать")
                                .addOption("Временные комнаты", MainMenu.tempRooms, "Информация о временных комнатах")
                                // .addOption("Билеты", MainMenu.tickets, "Информация о билетах")
                                .build())
                .queue();
    }
}
