package io.nexusbot.modules.verification;

import java.awt.Color;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnButtonDeletePress extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals(GlobalIds.DELETE_NOTIFICATION_MESSAGE_BUTTON.getValue())) {
            return;
        }
        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            EmbedUtil.replyEmbed(event, "Недостаточно прав.", Color.RED);
            return;
        }
        event.deferEdit().queue();
        event.getMessage().delete().queue();
    }

}
