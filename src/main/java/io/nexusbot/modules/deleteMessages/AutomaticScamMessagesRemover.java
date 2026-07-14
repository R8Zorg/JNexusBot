package io.nexusbot.modules.deleteMessages;

import java.awt.Color;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.entities.SpecialTextChannels;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.database.services.SpecialTextChannelsService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

class MessageInfo {
    public final String messageSignature;
    public final OffsetDateTime firstMessageTime;
    public Set<Long> channelIds = new HashSet<>();
    public int messagesCount = 1;
    public boolean targeted = false;

    public MessageInfo(String messageSignature, OffsetDateTime firstMessageTime, long channelId) {
        this.messageSignature = messageSignature;
        this.firstMessageTime = firstMessageTime;
        channelIds.add(channelId);
    }
}

@EventListeners
public class AutomaticScamMessagesRemover extends ListenerAdapter {
    private static final short MESSAGES_AMOUNT = 3;
    private static final int MESSAGES_HISTORY_POOL = 5;
    private static final int CACHE_INFO_LIVE_MINUTES = 3;

    private SpecialRolesService specialRolesService = new SpecialRolesService();
    private SpecialTextChannelsService specialTextChannelsService = new SpecialTextChannelsService();

    private Cache<Long, MessageInfo> sentMessages = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(CACHE_INFO_LIVE_MINUTES)).build();

    private void deleteMessages(Guild guild, long memberId, MessageInfo messageInfo) {
        List<GuildMessageChannel> channels = messageInfo.channelIds.stream()
                .map(id -> guild.getChannelById(GuildMessageChannel.class, id))
                .filter(Objects::nonNull)
                .toList();
        for (MessageChannel channel : channels) {
            channel.getHistory()
                    .retrievePast(MESSAGES_HISTORY_POOL)
                    .queue(history -> {
                        List<Message> messages = history.stream()
                                .filter(message -> message.getAuthor().getIdLong() == memberId)
                                .filter(message -> message.getTimeCreated().isAfter(messageInfo.firstMessageTime)
                                        || message.getTimeCreated().isEqual(messageInfo.firstMessageTime))
                                .filter(message -> getMessageSignature(message).equals(messageInfo.messageSignature))
                                .toList();
                        if (!messages.isEmpty()) {
                            channel.purgeMessages(messages);
                        }
                    });
        }
    }

    private String getMessageSignature(Message message) {
        StringBuilder messageContent = new StringBuilder(message.getContentRaw());
        List<Attachment> attachments = message.getAttachments();
        for (Attachment attachment : attachments) {
            messageContent.append(String.format(
                    "|%s|%d|%s|%d|%d",
                    attachment.getFileName(),
                    attachment.getSize(),
                    attachment.getContentType(),
                    attachment.getWidth(),
                    attachment.getHeight()));
        }
        return messageContent.toString();

    }

    private boolean isMessagesEqual(MessageInfo messageInfo, String messageSignature) {
        return messageInfo.messageSignature.equals(messageSignature);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        long userId = event.getAuthor().getIdLong();
        Message message = event.getMessage();
        long channelId = event.getChannel().getIdLong();
        String messageSignature = getMessageSignature(message);

        MessageInfo messageInfo = sentMessages.getIfPresent(userId);

        if (messageInfo == null) {
            sentMessages.put(userId, new MessageInfo(messageSignature, message.getTimeCreated(), channelId));
            return;
        }

        if (messageInfo.targeted
                && isMessagesEqual(messageInfo, messageSignature)) {
            event.getChannel().deleteMessageById(message.getIdLong()).queue();
            return;
        }

        if (!isMessagesEqual(messageInfo, messageSignature)) {
            sentMessages.put(userId, new MessageInfo(messageSignature, message.getTimeCreated(), channelId));
            return;
        }

        messageInfo.channelIds.add(channelId);
        messageInfo.messagesCount += 1;

        if (messageInfo.messagesCount >= MESSAGES_AMOUNT) {
            messageInfo.targeted = true;
            StringBuilder logMessage = new StringBuilder(
                    event.getAuthor().getAsMention() + " помечается за спам");

            Guild guild = event.getGuild();
            SpecialRoles specialRoles = specialRolesService.get(guild.getIdLong());

            Role muteRole = Optional.ofNullable(specialRoles)
                    .map(SpecialRoles::getMuteRoleId)
                    .map(guild::getRoleById)
                    .orElse(null);

            if (muteRole != null) {
                guild.addRoleToMember(event.getAuthor(), muteRole).queue();
                logMessage.append(" и получает мьют");
                // TODO: отправить сообщение в чат замьюченных, на котором можно нажать кнопку
                // для снятия роли, подтверждая смену пароля
            }

            logMessage.append(".");

            deleteMessages(guild, userId, messageInfo);

            TextChannel logChannel = Optional.ofNullable(specialTextChannelsService.get(guild.getIdLong()))
                    .map(SpecialTextChannels::getTextLogChannelId)
                    .map(guild::getTextChannelById)
                    .orElse(null);

            if (logChannel != null) {
                EmbedUtil.sendEmbed(logChannel, logMessage.toString(), Color.ORANGE);
            }
            return;
        }
    }
}
