package io.nexusbot.modules.globalCommands;

import java.awt.Color;
import java.util.List;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class Other {

    @Command(description = "Получить список всех участников, поставивших реакцию под сообщением")
    public void get(SlashCommandInteractionEvent event) {
    }

    @SubcommandGroup(parentName = "get")
    public void reacted(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "get reacted", description = "Получить список всех участников, поставивших реакцию под сообщением")
    public void users(SlashCommandInteractionEvent event,
            @Option(name = "message_id", description = "Айди сообщения") String messageId,
            @Option(name = "channel", description = "Канал, в котором находится сообщение", channelType = ChannelType.TEXT) TextChannel textChannel) {
        StringBuilder info = new StringBuilder();
        Message message = textChannel.retrieveMessageById(messageId).complete();
        message.getReactions()
                .forEach(reaction -> {
                    List<User> users = reaction.retrieveUsers().complete();
                    int counter = 1;
                    for (User user : users) {
                        info.append("【" + counter + "】" + user.getName() + "\n");
                        counter++;
                    }
                });
        EmbedUtil.replyEmbed(event, info.toString(), Color.GREEN);
    }
}
