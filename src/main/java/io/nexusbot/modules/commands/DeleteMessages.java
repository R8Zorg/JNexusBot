package io.nexusbot.modules.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class DeleteMessages {
    private final Logger LOGGER = LoggerFactory.getLogger(DeleteMessages.class);
    void deleteFromUser(MessageChannel channel, User author, int amount) {
        List<Message> messages = new ArrayList<>();
        channel.getIterableHistory()
                .forEachAsync(message -> {
                    if (message.getAuthor().equals(author))
                        messages.add(message);
                    return messages.size() < amount;
                })
                .thenRun(() -> channel.purgeMessages(messages));
    }

    @Command(description = "Удалить сообщения")
    @AdditionalSettings(defaultPermissions = Permission.MESSAGE_MANAGE)
    public void clear(SlashCommandInteractionEvent event,
            @Option(name = "amount", description = "Количество") Integer amount,
            @Option(name = "member", description = "Участник, чьи сообщения нужно удалить") Member member,
            @Option(name = "channel", description = "Канал, в котором нужно удалить сообщения", channelType = ChannelType.TEXT, required = false) TextChannel channel,
            @Option(name = "regex", description = "Содержание сообщения должно соответствовать этому правилу", required = false) String regex) {
        event.deferReply().setEphemeral(true).queue();
        List<Message> messages = new ArrayList<>();
        // MessageChannel messageChannel = channel != null ? channel : event.getChannel();
        MessageChannel messageChannel = event.getChannel();
        messageChannel.getIterableHistory()
                .forEachAsync(message -> {
                    if (message.getAuthor().equals(member.getUser()))
                        messages.add(message);
                    return messages.size() < amount;
                })
                .thenRun(() -> messageChannel.purgeMessages(messages))
                .thenRun(() -> EmbedUtil.replyEmbed(event.getHook(), "Удалено " + messages.size() + " сообщений", Color.GREEN));
    }
}
