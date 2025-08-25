package io.nexusbot.modules.listeners.tempRooms;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.componentsData.TempRoomSettingsModal;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnRoomSettingsModalSubmit extends ListenerAdapter {
    private final Map<String, Consumer<ModalInteractionEvent>> handlers = new HashMap<>();

    public OnRoomSettingsModalSubmit() {
        handlers.put(TempRoomSettingsModal.STATUS.getValue(), this::setStatus);
        handlers.put(TempRoomSettingsModal.LIMIT.getValue(), this::setLimit);
        handlers.put(TempRoomSettingsModal.BITRATE.getValue(), this::setBitrate);
        handlers.put(TempRoomSettingsModal.NAME.getValue(), this::setName);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        Consumer<ModalInteractionEvent> handler = handlers.get(event.getModalId());

        if (handler != null) {
            handler.accept(event);
        }
    }

    private String getModalValue(ModalInteractionEvent event) {
        return event.getValue(GlobalIds.BASE_MODAL_TEXT_INPUT_ID.getValue()).getAsString();
    }

    private void setStatus(ModalInteractionEvent event) {
        String status = getModalValue(event);
        event.getChannel().asVoiceChannel().modifyStatus(status).queue();
        EmbedUtil.replyEmbed(event, "Статус канала изменён на: `" + status + "`", Color.GREEN);
    }

    private void setLimit(ModalInteractionEvent event) {
        try {
            int limit = Integer.parseInt(getModalValue(event));
            event.getChannel().asVoiceChannel().getManager().setUserLimit(limit).queue();
            EmbedUtil.replyEmbed(event, "Лимит изменён на: `" + limit + "`", Color.GREEN);
        } catch (NumberFormatException e) {
            EmbedUtil.replyEmbed(event, "Введите корректное число", Color.RED);
            return;
        }
    }

    private void setBitrate(ModalInteractionEvent event) {
        String bitrateInput = getModalValue(event);
        try {
            int bitrate = Integer.parseInt(bitrateInput);
            if (bitrate < 8 || bitrate > 96) {
                EmbedUtil.replyEmbed(event, "Введено некорректное значение битрейта", Color.RED);
                return;
            }
            bitrate = bitrate * 1000;
            event.getChannel().asVoiceChannel().getManager().setBitrate(bitrate).queue();
            EmbedUtil.replyEmbed(event, "Битрейт канала изменён на: `" + bitrate + "` кб/с", Color.GREEN);
        } catch (NumberFormatException e) {
            EmbedUtil.replyEmbed(event, "Введите корректное число", Color.RED);
            return;
        }
    }

    private void setName(ModalInteractionEvent event) {
        String name = getModalValue(event);
        event.getChannel().asVoiceChannel().getManager().setName(name).queue();
        EmbedUtil.replyEmbed(event, "Название канала изменено на: `" + name + "`", Color.GREEN);
    }
}
