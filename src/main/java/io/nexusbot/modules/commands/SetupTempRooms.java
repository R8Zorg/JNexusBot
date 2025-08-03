package io.nexusbot.modules.commands;

import java.awt.Color;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.services.TempRoomCreatorService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class SetupTempRooms {
    private TempRoomCreatorService creatorService = new TempRoomCreatorService();

    @Command
    public void setup(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "setup")
    public void rooms(SlashCommandInteractionEvent event,
            @Option(name = "creator", description = "Voice creator", channelType = ChannelType.VOICE) VoiceChannel voiceChannelCreator,
            @Option(name = "category", description = "Category for temp room", channelType = ChannelType.CATEGORY) Category category) {
        try {
            event.deferReply(true).queue();
            TempRoomCreator roomCreator = creatorService.getOrCreate(voiceChannelCreator.getIdLong());
            roomCreator.setTempRoomCategoryId(category.getIdLong());
            creatorService.saveOrUpdate(roomCreator);
            EmbedUtil.replyEmbed(event.getHook(), "Successfully saved.", Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event.getHook(), "Couldn't save data: " + e.getMessage(), Color.RED);
        }
    }
}
