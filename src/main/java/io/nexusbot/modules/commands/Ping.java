package io.nexusbot.modules.commands;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class Ping {

    @Command(name = "ping", description = "Replys pong!")
    public void pong(SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue();
    }
}
