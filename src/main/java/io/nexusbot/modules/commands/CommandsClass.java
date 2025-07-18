package io.nexusbot.modules.commands;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.MissingAccessException;

@SlashCommands
public class CommandsClass {
    @Command(description = "Send a message")
    public void say(SlashCommandInteractionEvent event,
            @Option(name = "message", description = "Message to send") String message) {
        try {
        event.getChannel().sendMessage(message).queue(_ -> {
            event.reply("Message sent").setEphemeral(true).queue();
        });
        } catch (MissingAccessException e) {
            event.reply("Failed to send message: " + e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Command(description = "Reply a message")
    public void echo(SlashCommandInteractionEvent event,
            @Option(name = "message", description = "Message to send") String message) {
        event.reply(message).queue();
    }

    @Command(description = "Test command")
    public void test(SlashCommandInteractionEvent event,
            @Option(name = "string", required = false) String param1,
            @Option(name = "int", required = false) Integer param2,
            @Option(name = "long", required = false) Long param3,
            @Option(name = "bool", required = false) Boolean param4,
            @Option(name = "double", required = false) Double param5,
            @Option(name = "role", required = false) Role param6,
            @Option(name = "member", required = false) Member param7,
            @Option(name = "channel", required = false) Channel param8,
            @Option(name = "attachment", required = false) Attachment param9) {
        event.reply("Ok").queue();
    }
}
