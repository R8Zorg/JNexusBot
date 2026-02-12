package io.nexusbot.modules.globalCommands;

import java.awt.Color;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.nexusbot.utils.EmbedUtil;
import io.nexusbot.utils.MembersUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

@SlashCommands
public class ChangeNick {
    @Command(description = "Изменить никнейм на сервере")
    public void nickname(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "nickname", description = "Установить никнейм")
    public void set(SlashCommandInteractionEvent event,
            @Option(name = "nick", description = "Новый никнейм") String nickname) {
        if (MembersUtil.inBlacklist(event.getUser().getIdLong())) {
            EmbedUtil.replyEmbed(event, "Вам запрещено испольовать эту команду", Color.RED);
            return;
        }
        try {
            event.getMember().modifyNickname(nickname).queue(
                    success -> EmbedUtil.replyEmbed(event, "Ник успешно сменён", Color.GREEN),
                    error -> EmbedUtil.replyEmbed(event, "Возникла ошибка при смене никнейма: " + error.getMessage(),
                            Color.RED));
        } catch (HierarchyException e) {
            EmbedUtil.replyEmbed(event, "Невозможно изменить никнейм участнику с ролью, равной или превышающей мою.",
                    Color.RED);
        }
    }

    @Subcommand(parentNames = "nickname", description = "Сбросить никнейм")
    public void reset(SlashCommandInteractionEvent event) {
        if (MembersUtil.inBlacklist(event.getUser().getIdLong())) {
            EmbedUtil.replyEmbed(event, "Вам запрещено испольовать эту команду", Color.RED);
            return;
        }
        event.getMember().modifyNickname(null).queue(
                success -> EmbedUtil.replyEmbed(event, "Ник успешно сброшен", Color.GREEN),
                error -> EmbedUtil.replyEmbed(event, "Возникла ошибка при сбросе никнейма: " + error.getMessage(),
                        Color.RED));
    }
}
