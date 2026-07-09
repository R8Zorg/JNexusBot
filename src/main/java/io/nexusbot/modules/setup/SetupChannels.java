package io.nexusbot.modules.setup;

import java.awt.Color;
import java.util.function.Consumer;

import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import io.nexusbot.database.entities.SpecialTextChannels;
import io.nexusbot.database.services.SpecialTextChannelsService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class SetupChannels {
    private SpecialTextChannelsService specialTextChannelsService = new SpecialTextChannelsService();

    private void saveLogChannelAndReply(SlashCommandInteractionEvent event, Consumer<SpecialTextChannels> action) {
        var specialTextChannels = specialTextChannelsService.getOrCreate(event.getGuild().getIdLong());
        action.accept(specialTextChannels);
        specialTextChannelsService.saveOrUpdate(specialTextChannels);
        EmbedUtil.replyEmbed(event, "Сохранено.", Color.GREEN);
    }

    @SubcommandGroup(parentName = "setup", description = "Группа команд для указания текстовых каналов")
    public void channel(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "setup channel", description = "Канал для логов, связанных с сообщениями")
    public void text_log(SlashCommandInteractionEvent event,
            @Option(name = "channel", description = "Канал для текстовых логов", channelType = ChannelType.TEXT) TextChannel logChannel) {
        saveLogChannelAndReply(event, textChannels -> textChannels.setTextLogChannelId(logChannel.getIdLong()));
    }

    @Subcommand(parentNames = "setup channel", description = "Канал для логов, связанных с сообщениями")
    public void error_log(SlashCommandInteractionEvent event,
            @Option(name = "channel", description = "Канал для логов ошибок", channelType = ChannelType.TEXT) TextChannel logChannel) {
        saveLogChannelAndReply(event, textChannels -> textChannels.setErrorLogChannelId(logChannel.getIdLong()));
    }
}
