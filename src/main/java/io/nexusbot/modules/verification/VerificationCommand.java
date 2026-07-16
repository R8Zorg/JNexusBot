package io.nexusbot.modules.verification;

import java.awt.Color;
import java.util.Objects;
import java.util.Optional;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@SlashCommands
public class VerificationCommand {
    private SpecialRolesService rolesService = new SpecialRolesService();

    @Command(description = "Создать сообщение для верификации участников")
    public void verification(SlashCommandInteractionEvent event,
            @Option(name = "text", description = "Текст для эмбед сообщения", required = false) String text) {
        text = Objects.requireNonNullElse(text, "Нажмите на кнопку ниже для прохождения верификации.");
        if (!isDefaultRoleExists(event.getGuild())) {
            EmbedUtil.replyEmbed(event,
                    "Не найдена стандартная роль. Для использования этой команды укажите её через `/setup role default`",
                    Color.RED);
            return;
        }
        var embed = EmbedUtil.generateEmbed(text, Color.CYAN);
        event.getChannel().sendMessageEmbeds(embed)
                .addComponents(ActionRow.of(
                        Button.primary(GlobalIds.VERIFICATION_BUTTON.getValue(), "Пройти верификацию")))
                .queue(success -> EmbedUtil.replyEmbed(event, "Готово", Color.GREEN),
                        failure -> EmbedUtil.replyEmbed(event, "Ошибка: " + failure.getMessage(), Color.RED));
    }

    private boolean isDefaultRoleExists(Guild guild) {
        Role defaultRole = Optional.ofNullable(rolesService.get(guild.getIdLong()))
                .map(SpecialRoles::getDefaultRoleId)
                .map(guild::getRoleById)
                .orElse(null);
        return defaultRole == null
                ? false
                : true;

    }
}
