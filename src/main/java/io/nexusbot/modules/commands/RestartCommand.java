package io.nexusbot.modules.commands;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class RestartCommand {
    private Logger LOGGER = LoggerFactory.getLogger(RestartCommand.class);

    @Command(description = "Перезапустить бота")
    @AdditionalSettings(defaultPermissions = Permission.ADMINISTRATOR)
    public void restart(SlashCommandInteractionEvent event) {
        User user = event.getMember().getUser();
        LOGGER.warn("Admin <{}>({}) restarting bot", user.getName(), user.getIdLong());

        MessageEmbed embed = new EmbedBuilder()
                .setDescription("Перезапускаюсь.")
                .setColor(Color.GREEN)
                .build();
        event.replyEmbeds(embed).setEphemeral(true).complete();
        event.getJDA().shutdown();
        System.exit(0);
    }

}
