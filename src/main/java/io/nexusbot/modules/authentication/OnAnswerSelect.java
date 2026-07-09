package io.nexusbot.modules.authentication;

import java.awt.Color;
import java.util.Optional;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnAnswerSelect extends ListenerAdapter {
    private SpecialRolesService rolesService = new SpecialRolesService();

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals(GlobalIds.AUTHENTICATION_ID.getValue())) {
            return;
        }
        String selectedOptionId = event.getSelectedOptions().get(0).getValue();
        var parameters = selectedOptionId.split(" ");

        if ((parameters[0] + parameters[1]) != parameters[3]) {
            EmbedUtil.replyEmbed(event, "Неверный ответ.", Color.RED);
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
                    "Не найдена стандартная роль. Возможно, она была удалена. Обратитесь к администратору.",
                    Color.RED);
            return;
        }
        guild.addRoleToMember(event.getMember(), defaultRole).queue();

    }
}
