package io.nexusbot.modules.globalEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.services.SpecialRolesService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

record MessageInfo(long messageId, long channelId, String contentRaw) {
}

@EventListeners
public class AutomaticScumMessagesRemover extends ListenerAdapter {
    private final short messagesAmount = 3;
    private HashMap<Long, List<MessageInfo>> sentMessages = new HashMap<>();
    private SpecialRolesService specialRolesService = new SpecialRolesService();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        long userId = event.getAuthor().getIdLong();
        Message receivedMessage = event.getMessage();
        if (sentMessages.get(userId) == null) {
            sentMessages.put(userId, new ArrayList<MessageInfo>(List.of(
                    new MessageInfo(receivedMessage.getIdLong(), receivedMessage.getChannelIdLong(),
                            receivedMessage.getContentRaw()))));
        } else {
            List<MessageInfo> memberMessages = sentMessages.get(userId);
            String lastMessageContentRaw = memberMessages.getLast().contentRaw();

            if (lastMessageContentRaw.equals(receivedMessage.getContentRaw())) {
                memberMessages.add(new MessageInfo(receivedMessage.getIdLong(), receivedMessage.getChannelIdLong(),
                        receivedMessage.getContentRaw()));
                if (memberMessages.size() >= messagesAmount) {
                    Guild guild = event.getGuild();

                    SpecialRoles specialRoles = specialRolesService.get(guild.getIdLong());
                    if (specialRoles != null) {
                        Long muteRoleId = specialRoles.getMuteRoleId();
                        if (muteRoleId != null) {
                            Role muteRole = guild.getRoleById(muteRoleId);
                            if (muteRole != null) {
                                guild.addRoleToMember(event.getMember(), muteRole).queue();
                            }
                        }
                    }
                    for (MessageInfo messageInfo : memberMessages) {
                        GuildMessageChannel channel = guild.getChannelById(GuildMessageChannel.class,
                                messageInfo.channelId());
                        channel.deleteMessageById(messageInfo.messageId()).queue();
                    }
                    sentMessages.remove(userId);
                }
            } else {
                sentMessages.remove(userId);
            }
        }
    }

}
