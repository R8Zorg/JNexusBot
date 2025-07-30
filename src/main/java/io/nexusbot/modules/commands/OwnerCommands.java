package io.nexusbot.modules.commands;

import java.awt.Color;
import java.util.List;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.OwnerOnly;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.nexusbot.database.entities.BotOwner;
import io.nexusbot.database.services.BotOwnerService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class OwnerCommands {
    BotOwnerService botOwnerService = new BotOwnerService();

    @Command(description = "Owner commands")
    @OwnerOnly
    public void owners(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "owners", description = "Get list of bot's owners")
    public void list(SlashCommandInteractionEvent event) {
        try {
            List<BotOwner> owners = botOwnerService.getAll();
            if (owners.isEmpty()) {
                EmbedUtil.replyEmbed(event, "There are no owners", Color.YELLOW);
                return;
            }
            String replyMessage = "My owners:\n";
            for (BotOwner botOwner : owners) {
                replyMessage += "<@" + botOwner.getId() + ">\n";
            }
            EmbedUtil.replyEmbed(event, replyMessage, Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event, "An error occured: " + e.getMessage(), Color.RED);
        }
    }

    @Subcommand(parentNames = "owners", description = "Add user to bot's owners")
    public void add(SlashCommandInteractionEvent event,
            @Option(name = "user", description = "User to add as bot's owners") User user) {
        try {
            botOwnerService.add(user.getIdLong());
            EmbedUtil.replyEmbed(event, user.getAsMention() + " added to owners.", Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event, "An error occured while adding user " + user.getName() + " to DB.", Color.RED);
        }
    }

    @Subcommand(parentNames = "owners", description = "Remove user from bot's owners")
    public void remove(SlashCommandInteractionEvent event,
            @Option(name = "user", description = "User to remove from bot's owners") User user) {
        if (user.getIdLong() == event.getUser().getIdLong()) {
            EmbedUtil.replyEmbed(event, "You can't remove yourself from owners.", Color.RED);
            return;
        }
        try {
            botOwnerService.remove(user.getIdLong());
            EmbedUtil.replyEmbed(event, user.getAsMention() + " removed from owners.", Color.GREEN);
        } catch (IllegalArgumentException e) {
            EmbedUtil.replyEmbed(event, "Unable to remove " + user.getAsMention() + " because user is not the owner", Color.RED);
        } 
        catch (Exception e) {
            EmbedUtil.replyEmbed(event, "An error occured while removing user " + user.getName() + " from DB: " + e.getMessage(),
                    Color.RED);
        }
    }
}
