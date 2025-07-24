package io.nexusbot.modules.commands;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.MissingAccessException;

@SlashCommands
public class Say {
    private void replyOnSuccess(SlashCommandInteractionEvent event) {
        event.reply("Message sent").setEphemeral(true).queue();
    }

    @Command(description = "Send message on behalf of the bot")
    public void say(SlashCommandInteractionEvent event,
            @Option(name = "message", description = "Message to send") String message,
            @Option(name = "channel", description = "Text channel", required = false, channelType = ChannelType.TEXT) TextChannel channel) {
        try {
            if (channel != null) {
                channel.sendMessage(message).queue(_ -> replyOnSuccess(event));
            } else {
                event.getChannel().sendMessage(message).queue(_ -> replyOnSuccess(event));
            }
        } catch (MissingAccessException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }
}
