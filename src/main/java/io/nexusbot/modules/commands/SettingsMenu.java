package io.nexusbot.modules.commands;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.componentsData.MainMenu;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

@SlashCommands
public class SettingsMenu {
    @Command
    public void menu(SlashCommandInteractionEvent event) {
        event.reply("")
                .addActionRow(
                        StringSelectMenu.create(MainMenu.id)
                                .addOption("Ничего", GlobalIds.nothing, "Ничего не выбирать")
                                .addOption("Временные комнаты", MainMenu.tempRooms, "Информация о временных комнатах")
                                .addOption("Билеты", MainMenu.tickets, "Информация о билетах")
                                .build())
                .queue();
    }
}
