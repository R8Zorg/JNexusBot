package io.nexusbot.modules.globalCommands;

import java.awt.Color;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class Ping {

    @Command(name = "ping", description = "Replys pong!")
    public void pong(SlashCommandInteractionEvent event) {
        EmbedUtil.replyEmbed(event, "Pong!\nЗадержка: " + event.getJDA().getGatewayPing() + "мс", Color.GREEN);
    }
}
