package io.nexusbot.modules.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class ClearMessages {
    private void deleteMessages(SlashCommandInteractionEvent event, int messagesAmount, Member member,
            MessageChannel messageChannel) {
        List<Message> messages = new ArrayList<>();
        messageChannel.getIterableHistory()
                .forEachAsync(message -> {
                    if (member == null) {
                        messages.add(message);
                    } else if (message.getAuthor().equals(member.getUser())) {
                        messages.add(message);
                    }
                    return messages.size() < messagesAmount;
                })
                .thenRun(() -> messageChannel.purgeMessages(messages))
                .thenRun(() -> EmbedUtil.replyEmbed(event.getHook(), "Удалено " + messages.size() + " сообщений",
                        Color.GREEN));
    }

    @Command(description = "Удалить сообщения")
    @AdditionalSettings(defaultPermissions = Permission.MESSAGE_MANAGE)
    public void clear(SlashCommandInteractionEvent event,
            @Option(name = "amount", description = "Количество. Не больше 100") Integer amount,
            @Option(name = "member", description = "Участник, чьи сообщения нужно удалить", required = false) Member member,
            // @Option(name = "content", description = "Содержание сообщения должно соответствовать этому правилу", required = false) String content,
            // @Option(name = "everywhere", description = "Удалить сообщения во всех каналах", required = false) Boolean isEverywhere,
            @Option(name = "channel", description = "Канал, в котором нужно удалить сообщения", channelType = ChannelType.TEXT, required = false) TextChannel channel) {
        event.deferReply().setEphemeral(true).queue();

        int messagesAmount = amount <= 100 ? amount : 100;
        MessageChannel messageChannel = channel != null ? channel : event.getChannel();
        deleteMessages(event, messagesAmount, member, messageChannel);

    }
}
