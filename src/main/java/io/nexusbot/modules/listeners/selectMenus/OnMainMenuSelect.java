package io.nexusbot.modules.listeners.selectMenus;

import java.awt.Color;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.componentsData.MainMenu;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnMainMenuSelect extends ListenerAdapter {
    private void tempRooms(StringSelectInteractionEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Временные голосовые комнаты (каналы)")
                .setDescription("""
                        - Для создания или изменения настроек уже текущего канала-создателя пропишите `/setup rooms save`
                            Выберите канал, в который нужно зайти, чтобы создать новую комнату.
                            Выберите категорию, в которой будет создана новая комната.
                            Выберите необязательные аргументы по желанию.
                        - Для создания нескольких каналов-создателей сразу пропишите `/setup rooms add`
                        - Для получения информации о настройках временных комнат пропишите `/room help`
                            """)
                .setFooter("Это описание может быть неактуальным. Отдавайте предпочтение описанию параметров команды.")
                .setColor(Color.CYAN)
                .build();
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }

    private void tickets(StringSelectInteractionEvent event) {
        EmbedUtil.replyEmbed(event, "Настройка билетов в разработке", Color.CYAN);

    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals(MainMenu.id)) {
            return;
        }
        String selectedOptionId = event.getSelectedOptions().get(0).getValue();
        if (selectedOptionId.equals(GlobalIds.NOTHING.getValue())) {
            event.deferEdit().queue();
        } else if (selectedOptionId.equals(MainMenu.tempRooms)) {
            tempRooms(event);
        } else if (selectedOptionId.equals(MainMenu.tickets)) {
            tickets(event);
        }
    }
}
