package io.nexusbot.modules.globalEvents;

import java.awt.Color;
import java.util.List;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.entities.SpecialTextChannels;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.database.services.SpecialTextChannelsService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class AutoMute extends ListenerAdapter {
    private SpecialRolesService rolesService = new SpecialRolesService();
    private SpecialTextChannelsService textService = new SpecialTextChannelsService();
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
        if (event.getAuthor().isBot()) {
            return;
        }

        long guildId = event.getGuild().getIdLong();
        Guild guild = event.getGuild();
        long channelId = event.getChannel().getIdLong();
        Member member = event.getMember();

        SpecialTextChannels textChannels = textService.get(guildId);
        if (textChannels == null) {
            return;
        }
        Long muteChannelId = textChannels.getMuteChannelId();
        if (muteChannelId == null) {
            return;
        }
        GuildChannel muteChannel = guild.getGuildChannelById(muteChannelId);
        if (muteChannel == null) {
            return;
        }

        if (channelId != muteChannel.getIdLong()) {
            return;
        }

        SpecialRoles specialRoles = rolesService.get(guildId);
        if (specialRoles == null) {
            return;
        }
        Long muteRoleId = specialRoles.getMuteRoleId();
        if (muteRoleId == null) {
            return;
        }
        Role muteRole = guild.getRoleById(muteRoleId);
        if (muteRole == null) {
            return;
        }

        Long textLogChannelId = textChannels.getTextLogChannelId();
        TextChannel logChannel;
        if (textLogChannelId != null) {
            logChannel = guild.getTextChannelById(textLogChannelId);
        } else {
            logChannel = null;
        }

        guild.addRoleToMember(member, muteRole).queue(success -> {
        }, error -> {
            if (logChannel != null) {
                EmbedUtil.sendEmbed(logChannel,
                        "Не удалось выдать мьют-роль участнику " + member.getAsMention() + "\nОшибка: " + error,
                        Color.RED);
            }
        });

        Long defaultRoleId = specialRoles.getDefaultRoleId();
        Role defaultRole = defaultRoleId != null
                ? guild.getRoleById(defaultRoleId)
                : null;
        final Role targetRole = defaultRole != null
                ? defaultRole
                : guild.getPublicRole();

        List<MessageChannel> channels = guild.getChannels().stream()
                .filter(_channel -> _channel instanceof MessageChannel)
                .filter(_channel -> targetRole.hasPermission(_channel, Permission.MESSAGE_SEND))
                .map(_channel -> (MessageChannel) _channel)
                .toList();
        long memberId = event.getAuthor().getIdLong();
        String scamContent = event.getMessage().getContentRaw();
        for (MessageChannel _channel : channels) {
            deleteMessages(_channel, memberId, scamContent);
        }

        if (logChannel != null) {
            EmbedUtil.sendEmbed(logChannel,
                    member.getAsMention() + " `(" + member.getIdLong() + ")`" + " получает мьют за скам рассылку.",
                    Color.ORANGE);
        }
    }

}
