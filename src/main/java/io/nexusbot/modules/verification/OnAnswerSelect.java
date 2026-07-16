package io.nexusbot.modules.verification;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Optional;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.entities.SpecialTextChannels;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.database.services.SpecialTextChannelsService;
import io.nexusbot.database.services.VerificationService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@EventListeners
public class OnAnswerSelect extends ListenerAdapter {
    private SpecialRolesService rolesService = new SpecialRolesService();
    private SpecialTextChannelsService channelsService = new SpecialTextChannelsService();
    private VerificationService verificationService = new VerificationService();
    private static final int MAX_ATTEMPTS = 3;
    private static final int TIMEOUT_MINUTES = 20;

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals(GlobalIds.VERIFICATION_BUTTON.getValue())) {
            return;
        }
        Member member = event.getMember();
        long memberId = member.getIdLong();
        long guildId = event.getGuild().getIdLong();

        String selectedOptionId = event.getSelectedOptions().get(0).getValue();
        var parameters = selectedOptionId.split(" ");
        int num1 = Integer.parseInt(parameters[0]);
        int num2 = Integer.parseInt(parameters[1]);
        int rightAnswer = Integer.parseInt(parameters[2]);

        var verification = verificationService.getOrCreate(memberId, guildId);

        OffsetDateTime userTimeout = verification.getMemberTimeout();
        if (userTimeout != null && OffsetDateTime.now().isBefore(userTimeout)) {
            EmbedUtil.replyEmbed(event, "Попытки сбросятся <t:" + userTimeout.toEpochSecond() + ":R>", Color.RED);
            return;
        }
        if (num1 + num2 != rightAnswer) {
            verification.addMemberAttempts(1);
            int userAttempts = verification.getMemberAttempts();

            if (userAttempts % MAX_ATTEMPTS == 0) {
                var timeoutEnd = OffsetDateTime.now().plusSeconds(TIMEOUT_MINUTES);
                var embed = EmbedUtil.generateEmbed(
                        member.getAsMention() + " не справляется с заданием с " + userAttempts + "й попытки.\n"
                                + "Попытки сбросятся <t:" + timeoutEnd.toEpochSecond() + ":R>",
                        Color.ORANGE);

                event.getChannel().sendMessageEmbeds(embed)
                        .addComponents(ActionRow.of(
                                Button.danger(GlobalIds.DELETE_NOTIFICATION_MESSAGE_BUTTON.getValue(), "✖️")))
                        .queue();
                verification.setMemberTimeout(timeoutEnd);
            }
            EmbedUtil.replyEmbed(event,
                    "Неверный ответ. Осталось попыток: "
                            + (MAX_ATTEMPTS - (userAttempts % MAX_ATTEMPTS)) % MAX_ATTEMPTS,
                    Color.RED);
            verificationService.saveOrUpdate(verification);
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
    }
}
