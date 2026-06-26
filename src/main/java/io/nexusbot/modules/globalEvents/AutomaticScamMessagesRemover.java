package io.nexusbot.modules.globalEvents;

import java.awt.Color;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.entities.SpecialTextChannels;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.database.services.SpecialTextChannelsService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

record MessageInfo(long channelId, String messageContent, OffsetDateTime messageCreationTime) {
}

@EventListeners
public class AutomaticScamMessagesRemover extends ListenerAdapter {
    private static final short MESSAGES_AMOUNT = 2;
    private static final int MESSAGES_HISTORY_POOL = 5;
    private static final long MINIMAL_MESSAGE_DELAY_MS = 600;

    private Map<Long, List<MessageInfo>> sentMessages = new ConcurrentHashMap<>();
    private SpecialRolesService specialRolesService = new SpecialRolesService();
    private SpecialTextChannelsService specialTextChannelsService = new SpecialTextChannelsService();

    private void deleteMessages(MessageChannel channel, long memberId, String contentRaw,
            OffsetDateTime firstMessageCreationTime) {
        channel.getHistory()
                .retrievePast(MESSAGES_HISTORY_POOL)
                .queue(history -> {
                    history.stream()
                            .filter(message -> message.getAuthor().getIdLong() == memberId)
                            .filter(message -> message.getContentRaw().equals(contentRaw))
                            .filter(message -> message.getTimeCreated().isAfter(firstMessageCreationTime)
                                    || message.getTimeCreated().isEqual(firstMessageCreationTime))
                            .forEach(message -> channel.deleteMessageById(message.getIdLong()).queue());
                });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        long userId = event.getAuthor().getIdLong();
        String receivedContentRaw = event.getMessage().getContentRaw();
        Message receivedMessage = event.getMessage();
        long receivedChannelId = event.getChannel().getIdLong();

        MessageInfo messageInfo = new MessageInfo(receivedChannelId, receivedMessage.getContentRaw(),
                receivedMessage.getTimeCreated());

        if (sentMessages.get(userId) == null) {
            sentMessages.put(userId, new ArrayList<MessageInfo>(
                    List.of(messageInfo)));
        } else {
            List<MessageInfo> messagesInfo = sentMessages.get(userId);
            MessageInfo lastMessageInfo = messagesInfo.getLast();

            long messageDelayMs = Duration.between(
                    lastMessageInfo.messageCreationTime(),
                    receivedMessage.getTimeCreated()).toMillis();
            if (messageDelayMs > MINIMAL_MESSAGE_DELAY_MS) {
                sentMessages.remove(userId);
                return;
            }

            if (lastMessageInfo.channelId() != receivedChannelId
                    && lastMessageInfo.messageContent().equals(receivedContentRaw)) {
                messagesInfo.add(messageInfo);
                if (messagesInfo.size() >= MESSAGES_AMOUNT) {
                    Guild guild = event.getGuild();
                    String logMessage = event.getAuthor().getAsMention() + " помечается за рассылку скам сообщений";

                    SpecialRoles specialRoles = specialRolesService.get(guild.getIdLong());
                    if (specialRoles != null) {
                        Long muteRoleId = specialRoles.getMuteRoleId();
                        if (muteRoleId != null) {
                            Role muteRole = guild.getRoleById(muteRoleId);
                            if (muteRole != null) {
                                guild.addRoleToMember(event.getAuthor(), muteRole).queue();
                                logMessage += " и получает мьют";
                            }
                        }
                    }
                    logMessage += ".";

                    Long defaultRoleId = specialRoles.getDefaultRoleId();
                    Role defaultRole = defaultRoleId != null
                            ? guild.getRoleById(defaultRoleId)
                            : null;
                    Role targetRole = defaultRole != null
                            ? defaultRole
                            : guild.getPublicRole();

                    List<MessageChannel> channels = guild.getChannels().stream()
                            .filter(_channel -> _channel instanceof MessageChannel)
                            .filter(_channel -> targetRole.hasPermission(_channel, Permission.MESSAGE_SEND))
                            .map(_channel -> (MessageChannel) _channel)
                            .toList();
                    OffsetDateTime firstMessageSentTime = messagesInfo.getFirst().messageCreationTime();
                    for (MessageChannel _channel : channels) {
                        deleteMessages(_channel, userId, receivedContentRaw, firstMessageSentTime);
                    }
                    sentMessages.remove(userId);

                    SpecialTextChannels specialTextChannels = specialTextChannelsService.get(guild.getIdLong());
                    if (specialTextChannels != null) {
                        TextChannel logChannel = guild.getTextChannelById(specialTextChannels.getTextLogChannelId());
                        if (logChannel != null) {
                            EmbedUtil.sendEmbed(logChannel, logMessage, Color.ORANGE);
                        }
                    }
                }
            } else {
                sentMessages.remove(userId);
            }
        }
    }

}
