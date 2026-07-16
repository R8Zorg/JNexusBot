package io.nexusbot.modules.deleteMessages;

import java.awt.Color;
import java.util.Optional;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.entities.SpecialTextChannels;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.database.services.SpecialTextChannelsService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnButtonPress extends ListenerAdapter {
    private SpecialRolesService specialRolesService = new SpecialRolesService();
    private SpecialTextChannelsService specialTextChannelsService = new SpecialTextChannelsService();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] componentsId = event.getComponentId().split(" ");
        if (!componentsId[0].equals(GlobalIds.UNMUTE_BUTTON.getValue())) {
            return;
        }
        long targetMemberId = Long.parseLong(componentsId[1]);
        Member interractedMember = event.getMember();

        if (interractedMember.getIdLong() != targetMemberId) {
            EmbedUtil.replyEmbed(event, "Эта кнопка не для Вас.", Color.RED);
            return;
        }

        Guild guild = event.getGuild();
        Role muteRole = Optional.ofNullable(specialRolesService.get(guild.getIdLong()))
                .map(SpecialRoles::getMuteRoleId)
                .map(guild::getRoleById)
                .orElse(null);
        if (muteRole == null) {
            EmbedUtil.replyEmbed(event, "Не удалось найти мьют роль на сервере. Обратитесь к администратору.",
                    Color.RED);
        }

        guild.removeRoleFromMember(interractedMember, muteRole).queue(
                success -> {
                    EmbedUtil.replyEmbed(event, "Роль успешно снята.", Color.GREEN);
                    event.getMessage().delete().queue();
                    TextChannel logChannel = Optional.ofNullable(specialTextChannelsService.get(guild.getIdLong()))
                            .map(SpecialTextChannels::getTextLogChannelId)
                            .map(guild::getTextChannelById)
                            .orElse(null);
                    if (logChannel != null) {
                        EmbedUtil.sendEmbed(logChannel,
                                interractedMember.getAsMention() + " убирает с себя мьют роль по нажатию кнопки.",
                                Color.CYAN);
                    }
                },
                failure -> EmbedUtil.replyEmbed(event, "Не удалось снять роль: " + failure.getMessage(), Color.RED));
    }
}
