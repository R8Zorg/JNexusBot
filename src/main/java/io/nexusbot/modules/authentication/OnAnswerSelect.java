package io.nexusbot.modules.authentication;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnAnswerSelect extends ListenerAdapter {
    private SpecialRolesService rolesService = new SpecialRolesService();
    private SpecialTextChannelsService channelsService = new SpecialTextChannelsService();
    private Map<Long, Integer> userAttempts = new ConcurrentHashMap<>();
    private Map<Long, OffsetDateTime> userTimeouts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 3;
    private static final int TIMEOUT_MINUTES = 30;

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals(GlobalIds.AUTHENTICATION_ID.getValue())) {
            return;
        }
        Member member = event.getMember();
        long memberId = member.getIdLong();

        OffsetDateTime userTimeout = userTimeouts.get(memberId);
        if (userTimeout != null && OffsetDateTime.now().isBefore(userTimeout)) {
            return;
        }

        String selectedOptionId = event.getSelectedOptions().get(0).getValue();
        var parameters = selectedOptionId.split(" ");
        int num1 = Integer.parseInt(parameters[0]);
        int num2 = Integer.parseInt(parameters[1]);
        int rightAnswer = Integer.parseInt(parameters[2]);

        if (num1 + num2 != rightAnswer) {
            Integer userAttemp = userAttempts.get(memberId);
            if (userAttemp == null) {
                userAttemp = 1;
            } else {
                userAttemp += 1;
            }
            userAttempts.put(memberId, userAttemp);

            if (userAttemp >= MAX_ATTEMPTS) {
                var timeoutEnd = OffsetDateTime.now().plusMinutes(TIMEOUT_MINUTES);
                EmbedUtil.sendEmbed(event.getChannel(),
                        member.getAsMention() + " не справляется с заданием " + userAttemp + "й раз.\n"
                                + "Попытки сбросятся <t:" + timeoutEnd.toEpochSecond() + ":R>",
                        Color.ORANGE);
                userTimeouts.put(memberId, timeoutEnd);
            } else {
                EmbedUtil.replyEmbed(event,
                        "Неверный ответ. Осталось попыток: " + (MAX_ATTEMPTS - userAttemp),
                        Color.RED);
            }
            return;
        }
        event.deferReply(true).queue();
        Guild guild = event.getGuild();
        Role defaultRole = Optional.ofNullable(rolesService.get(guild.getIdLong()))
                .map(SpecialRoles::getDefaultRoleId)
                .map(guild::getRoleById)
                .orElse(null);
        if (defaultRole == null) {
            EmbedUtil.replyEmbed(event.getHook(),
                    "Не найдена стандартная роль. Вероятно, она была удалена. Обратитесь к администратору.",
                    Color.RED);
            return;
        }
        try {
            guild.addRoleToMember(event.getMember(), defaultRole).queue();
        } catch (PermissionException error) {
            MessageChannel logChannel = Optional.ofNullable(channelsService.get(guild.getIdLong()))
                    .map(SpecialTextChannels::getErrorLogChannelId)
                    .map(guild::getTextChannelById)
                    .orElse(null);
            if (logChannel != null) {
                EmbedUtil.sendEmbed(logChannel,
                        "При попытке выдать стандартную роль после успешной аутентификации произошла ошибка:\n"
                                + error.getMessage(),
                        Color.RED);
            }
            EmbedUtil.replyEmbed(event.getHook(),
                    "При попытке выдать роль произошла ошибка. Обратитесь к администратору.",
                    Color.RED);
        }
        EmbedUtil.replyEmbed(event.getHook(), "Доступ выдан.", Color.GREEN);
        userAttempts.remove(memberId);
        userTimeouts.remove(memberId);

    }
}
