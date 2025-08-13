package io.nexusbot.utils;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class EmbedUtil {
    public static void replyEmbed(SlashCommandInteractionEvent event, String description, Color color) {
        MessageEmbed embed = new EmbedBuilder()
                .setDescription(description)
                .setColor(color)
                .build();
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }

    public static void replyEmbed(SlashCommandInteractionEvent event, String description, Color color,
            boolean ephemeral) {
        MessageEmbed embed = new EmbedBuilder()
                .setDescription(description)
                .setColor(color)
                .build();
        event.replyEmbeds(embed).setEphemeral(ephemeral).queue();
    }

    public static void replyEmbed(InteractionHook hook, String description, Color color) {
        MessageEmbed embed = new EmbedBuilder()
                .setDescription(description)
                .setColor(color)
                .build();
        hook.sendMessageEmbeds(embed).queue();
    }

    public static void replyEmbed(StringSelectInteractionEvent event, String description, Color color) {
        MessageEmbed embed = new EmbedBuilder()
                .setDescription(description)
                .setColor(color)
                .build();
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }

    public static void sendEmbed(MessageChannel channel, String description, Color color) {
        MessageEmbed embed = new EmbedBuilder()
                .setDescription(description)
                .setColor(color)
                .build();
        channel.sendMessageEmbeds(embed).queue();
    }
}
