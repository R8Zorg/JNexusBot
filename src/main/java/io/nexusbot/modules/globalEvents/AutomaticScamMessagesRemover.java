package io.nexusbot.modules.globalEvents;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.entities.SpecialTextChannels;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.database.services.SpecialTextChannelsService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class AutomaticScamMessagesRemover extends ListenerAdapter {
    private static final short MESSAGES_AMOUNT = 2;
    private HashMap<Long, List<String>> sentMessages = new HashMap<>();
    private SpecialRolesService specialRolesService = new SpecialRolesService();
    private SpecialTextChannelsService specialTextChannelsService = new SpecialTextChannelsService();
    private static final int MESSAGES_HISTORY_POOL = 5;

    private void deleteMessages(MessageChannel channel, long memberId, String contentRaw) {
        channel.getHistory()
                .retrievePast(MESSAGES_HISTORY_POOL)
                .queue(history -> {
                    history.stream()
                            .filter(message -> message.getAuthor().getIdLong() == memberId)
                            .filter(message -> message.getContentRaw().equals(contentRaw))
                            .forEach(message -> channel.deleteMessageById(message.getIdLong()).queue());
                });
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        long userId = event.getAuthor().getIdLong();
        String receivedContentRaw = event.getMessage().getContentRaw();
        if (sentMessages.get(userId) == null) {
            sentMessages.put(userId, new ArrayList<String>(
                    List.of(receivedContentRaw)));
        } else {
            List<String> messagesContentRaw = sentMessages.get(userId);
            String lastMessageContentRaw = messagesContentRaw.getLast();

            if (lastMessageContentRaw.equals(receivedContentRaw)) {
                messagesContentRaw.add(receivedContentRaw);
                if (messagesContentRaw.size() >= MESSAGES_AMOUNT) {
                    Guild guild = event.getGuild();
                    String logMessage = event.getAuthor().getAsMention() + " помечен(а) за рассылку скама";

                    SpecialRoles specialRoles = specialRolesService.get(guild.getIdLong());
                    if (specialRoles != null) {
                        Long muteRoleId = specialRoles.getMuteRoleId();
                        if (muteRoleId != null) {
                            Role muteRole = guild.getRoleById(muteRoleId);
                            if (muteRole != null) {
                                guild.addRoleToMember(event.getAuthor(), muteRole).queue();
                                logMessage += " и был замьючен";
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
                    for (MessageChannel _channel : channels) {
                        deleteMessages(_channel, userId, receivedContentRaw);
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
