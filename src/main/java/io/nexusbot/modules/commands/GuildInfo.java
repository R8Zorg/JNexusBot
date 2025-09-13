package io.nexusbot.modules.commands;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

// @SlashCommands
public class GuildInfo {
    @Command(description = "Get info about this guild")
    public void guild(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "guild", description = "Send guild's members count")
    public void members_count(SlashCommandInteractionEvent event) {
        int membersCount = event.getGuild().getMemberCount();
        event.reply("This guild have " + membersCount + " members!").queue();
    }

    @SubcommandGroup(parentName = "guild")
    public void get(SlashCommandInteractionEvent event) {
    }

    private void sendGuildOwner(SlashCommandInteractionEvent event, Member owner) {
        event.reply("The owner of this guild is " + owner.getAsMention())
                .setEphemeral(true)
                .queue();
    }

    private void sendErrorOnRetrievingOwner(SlashCommandInteractionEvent event) {
        event.reply("Failed to retrieve the guild owner")
                .setEphemeral(true)
                .queue();
    }

    @Subcommand(parentNames = "guild get")
    public void owner(SlashCommandInteractionEvent event) {
        event.getGuild().retrieveOwner().queue(owner -> sendGuildOwner(event, owner),
                error -> sendErrorOnRetrievingOwner(event));
    }

    @SubcommandGroup(parentName = "guild")
    public void add(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "guild add", name = "owner")
    public void add_owner(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Member to add") Member member) {
        // some code
        event.reply(member.getAsMention() + " added to this guild owners").setEphemeral(true).queue();
    }
}
