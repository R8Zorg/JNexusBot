package io.nexusbot.modules.globalCommands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

import io.github.r8zorg.jdatools.annotations.AdditionalSettings;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class ClearMessages {
    private final int DELAY = 400;

    private void processMessagesDeleting(
            List<MessageChannel> channels, int index,
            int amount, Member member,
            AtomicInteger totalDeleted, SlashCommandInteractionEvent event,
            long progressMessageId, ScheduledExecutorService executor) {
        if (index >= channels.size()) {
            event.getHook()
                    .editMessageEmbedsById(progressMessageId,
                            EmbedUtil.generateEmbed(
                                    "Очистка завершена." +
                                            "\nКаналов обработано: " + channels.size() +
                                            "\nУдалено сообщений: " + totalDeleted.get() +
                                            "\n(Сообщения старше 14 дней (если были) пропущены)",
                                    Color.GREEN))
                    .queue();
            return;
        }

        MessageChannel channel = channels.get(index);

        deleteMessages(channel, amount, member, messagesDeleted -> {
            totalDeleted.addAndGet(messagesDeleted);
        });
        event.getHook()
                .editMessageEmbedsById(progressMessageId,
                        EmbedUtil.generateEmbed(
                                "Обработано каналов: " + (index + 1) + "/" + channels.size() +
                                        "\nУдалено сообщений: " + totalDeleted.get(),
                                Color.CYAN))
                .queue();
        executor.schedule(() -> {
            processMessagesDeleting(channels, index + 1, amount, member, totalDeleted, event, progressMessageId,
                    executor);
        }, DELAY, TimeUnit.MILLISECONDS);
    }

    private void deleteMessages(MessageChannel messageChannel, int messagesAmount, Member member,
            IntConsumer callback) {
        OffsetDateTime dateLimit = OffsetDateTime.now().plusDays(14);

        messageChannel.getHistory()
                .retrievePast(messagesAmount)
                .queue(history -> {
                    List<Message> messages = history.stream()
                            .filter(message -> message.getTimeCreated().isBefore(dateLimit))
                            .filter(message -> member == null
                                    || message.getAuthor().equals(member.getUser()))
                            .toList();
                    if (!messages.isEmpty()) {
                        messageChannel.purgeMessages(messages);
                    }
                    if (callback != null) {
                        callback.accept(messages.size());
                    }
                });
    }

    @Command(description = "Категория для удаления сообщений")
    @AdditionalSettings(defaultPermissions = Permission.MESSAGE_MANAGE)
    public void clear(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "clear", description = "Удалить сообщения в текущем канале")
    public void messages(SlashCommandInteractionEvent event,
            @Option(name = "amount", description = "Количество. Не больше 100") Integer amount,
            @Option(name = "member", description = "Участник, чьи сообщения нужно удалить", required = false) Member member,
            // @Option(name = "content", description = "Содержание сообщения должно
            // соответствовать этому правилу", required = false) String content,
            @Option(name = "channel", description = "Канал, в котором нужно удалить сообщения", channelType = ChannelType.TEXT, required = false) TextChannel channel) {
        event.deferReply().setEphemeral(true).queue();

        int messagesAmount = Math.min(amount, 100);

        MessageChannel targetChannel = channel != null ? channel : event.getChannel();
        deleteMessages(targetChannel, messagesAmount, member,
                deletedAmount -> EmbedUtil.replyEmbed(event.getHook(),
                        "Удалено сообщений: " + deletedAmount
                                + "\n(Сообщения старше 14 дней (если были) пропущены)",
                        Color.GREEN));
    }

    @Subcommand(parentNames = "clear", description = "Удалить сообщения во всех каналах, доступных указанной роли")
    public void spam(SlashCommandInteractionEvent event,
            @Option(name = "amount", description = "Количество. Не больше 100") Integer amount,
            @Option(name = "member", description = "Участник, чьи сообщения нужно удалить", required = false) Member member,
            // @Option(name = "content", description = "Содержание сообщения должно
            // соответствовать этому правилу", required = false) String content,
            @Option(name = "role", description = "Роль, дающая спамеру писать в каналах. По стандарту используется everyone", required = false) Role role) {
        event.deferReply().setEphemeral(true).queue();

        int messagesAmount = Math.min(amount, 100);
        Guild guild = event.getGuild();
        final Role targetRole = role != null
                ? role
                : guild.getPublicRole();

        List<MessageChannel> channels = guild.getChannels().stream()
                .filter(channel -> channel instanceof MessageChannel)
                .filter(channel -> targetRole.hasPermission(channel, Permission.MESSAGE_SEND))
                .map(channel -> (MessageChannel) channel)
                .toList();

        AtomicInteger totalMessagesDeleted = new AtomicInteger();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        event.getHook()
                .sendMessageEmbeds(
                        EmbedUtil.generateEmbed(
                                "Обработано каналов: 0" + "/" + channels.size() +
                                        "Удалено сообщений: 0/" + amount,
                                Color.CYAN))
                .queue(progressMessage -> {
                    processMessagesDeleting(channels, 0, messagesAmount, member,
                            totalMessagesDeleted,
                            event,
                            progressMessage.getIdLong(), executor);
                });
    }
}
