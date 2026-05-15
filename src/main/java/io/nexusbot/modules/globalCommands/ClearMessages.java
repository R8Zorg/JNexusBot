package io.nexusbot.modules.globalCommands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class ClearMessages {
    private void deleteMessages(MessageChannel messageChannel, int messagesAmount, Member member,
            IntConsumer callback) {
        List<Message> messages = new ArrayList<>();
        OffsetDateTime dateLimit = OffsetDateTime.now().minusDays(14);

        messageChannel.getIterableHistory()
                .forEachAsync(message -> {
                    if (message.getTimeCreated().isBefore(dateLimit)) {
                        return true;
                    }
                    if (member == null || message.getAuthor().equals(member.getUser())) {
                        messages.add(message);
                    }
                    return messages.size() < messagesAmount;
                })
                .thenRun(() -> {
                    if (!messages.isEmpty()) {
                        messageChannel.purgeMessages(messages);
                    }
                    if (callback != null) {
                        callback.accept(messages.size());
                    }
                });
    }

    @Command(description = "Удалить сообщения")
    @AdditionalSettings(defaultPermissions = Permission.MESSAGE_MANAGE)
    public void clear(SlashCommandInteractionEvent event,
            @Option(name = "amount", description = "Количество. Не больше 100") Integer amount,
            @Option(name = "member", description = "Участник, чьи сообщения нужно удалить", required = false) Member member,
            // @Option(name = "content", description = "Содержание сообщения должно
            // соответствовать этому правилу", required = false) String content,
            // @Option(name = "everywhere", description = "Удалить сообщения во всех каналах?", required = false) Boolean everywhere,
            @Option(name = "channel", description = "Канал, в котором нужно удалить сообщения", channelType = ChannelType.TEXT, required = false) TextChannel channel) {
        event.deferReply().setEphemeral(true).queue();

        int messagesAmount = Math.min(amount, 100);

        MessageChannel targetChannel = channel != null ? channel : event.getChannel();
        deleteMessages(targetChannel, messagesAmount, member,
            deletedAmount -> EmbedUtil.replyEmbed(event.getHook(),
                "Удалено сообщений: " + deletedAmount
                + "\n(Сообщения старше 14 дней (если были) пропущены)",
                Color.GREEN));
        return;
        // if (everywhere == null || !everywhere) {
        // }

        // Guild guild = event.getGuild();
        // List<MessageChannel> messageChannels = guild.getChannels().stream()
        //         .filter(_channel -> _channel instanceof MessageChannel)
        //         .map(_channel -> (MessageChannel) _channel)
        //         .toList();
        // AtomicInteger completedChannels = new AtomicInteger();
        // AtomicInteger totalMessagesDeleted = new AtomicInteger();
        //
        // for (MessageChannel targetChannel : messageChannels) {
        //     deleteMessages(targetChannel, messagesAmount, member,
        //             deletedAmount -> {
        //                 totalMessagesDeleted.addAndGet(deletedAmount);
        //                 if (completedChannels.incrementAndGet() == messageChannels.size()) {
        //                     EmbedUtil.replyEmbed(
        //                             event.getHook(),
        //                             "Очистка завершена." +
        //                                     "\nКаналов обработано: " + completedChannels.get() +
        //                                     "\nУдалено сообщений: " + totalMessagesDeleted +
        //                                     "\n(Сообщения старше 14 дней (если были) пропущены)",
        //                             Color.GREEN);
        //                 }
        //             });
        // }

    }
}
