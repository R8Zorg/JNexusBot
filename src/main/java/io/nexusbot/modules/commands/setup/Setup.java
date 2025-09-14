package io.nexusbot.modules.commands.setup;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class Setup {
    @Command(description = "Команды для настройки бота")
    @AdditionalSettings(defaultPermissions = Permission.ADMINISTRATOR)
    public void setup(SlashCommandInteractionEvent event) {
    }

}
