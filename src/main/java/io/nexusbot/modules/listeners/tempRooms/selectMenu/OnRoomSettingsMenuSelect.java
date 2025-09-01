package io.nexusbot.modules.listeners.tempRooms.selectMenu;

import java.awt.Color;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.componentsData.TempRoomSettingsMenu;
import io.nexusbot.componentsData.TempRoomSettingsModal;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.database.services.TempRoomSettingsService;
import io.nexusbot.utils.EmbedUtil;
import io.nexusbot.utils.ModalUtil;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnRoomSettingsMenuSelect extends ListenerAdapter {
    private TempRoomService tempRoomService = new TempRoomService();
    private TempRoomSettingsService tempRoomSettingsService = new TempRoomSettingsService();

    private void setStatus(StringSelectInteractionEvent event) {
        event.replyModal(ModalUtil.generateModal(
                TempRoomSettingsModal.STATUS.getValue(), "Изменить статус канала",
                "Введите новый статус", "Статус канала", 1, 128))
                .queue();
    }

    private void setLimit(StringSelectInteractionEvent event) {
        event.replyModal(ModalUtil.generateModal(
                TempRoomSettingsModal.LIMIT.getValue(), "Изменить лимит участников",
                "Введите новый лимит", "0 - 99", 1, 2))
                .queue();
    }

    private void setBitrate(StringSelectInteractionEvent event) {
        event.replyModal(ModalUtil.generateModal(
                TempRoomSettingsModal.BITRATE.getValue(), "Изменить битрейт канала",
                "Введите новый битрейт", "8 - 96", 1, 3))
                .queue();
    }

    private void setNsfw(StringSelectInteractionEvent event, TempRoomSettings tempRoomSettings) {
        VoiceChannel voiceChannel = event.getChannel().asVoiceChannel();
        boolean nsfw = !voiceChannel.isNSFW();
        voiceChannel.getManager().setNSFW(nsfw).queue();
        if (nsfw) {
            EmbedUtil.replyEmbed(event, "Каналу выставлено ограничение 18+", Color.GREEN);
        } else {
            EmbedUtil.replyEmbed(event, "С канала снято ограничение 18+", Color.GREEN);
        }
        tempRoomSettings.setNsfw(nsfw);
        tempRoomSettingsService.saveOrUpdate(tempRoomSettings);
    }

    private void setOwnership(StringSelectInteractionEvent event) {
        EmbedUtil.replyEmbed(event, "Эта функция ещё в разработке", Color.ORANGE);
    }

    private void setName(StringSelectInteractionEvent event) {
        event.replyModal(ModalUtil.generateModal(
                TempRoomSettingsModal.NAME.getValue(), "Изменить название канала",
                "Введите новое название", "название канала", 1, 128))
                .queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals(TempRoomSettingsMenu.ID)) {
            return;
        }
        TempRoom tempRoom = tempRoomService.get(event.getChannel().getIdLong());
        if (tempRoom == null) {
            return;
        }
        long ownerId = event.getMember().getIdLong();
        if (ownerId != tempRoom.getOwnerId()) {
            EmbedUtil.replyEmbed(event, "Взаимодействовать со списками может только владелец комнаты", Color.RED);
            return;
        }

        String selectedOptionId = event.getSelectedOptions().get(0).getValue();
        if (selectedOptionId.equals(GlobalIds.NOTHING.getValue())) {
            event.deferEdit().queue();
            return;
        }

        TempRoomSettings tempRoomSettings = tempRoomSettingsService.getOrCreate(ownerId, event.getGuild().getIdLong());
        switch (TempRoomSettingsMenu.fromValue(selectedOptionId)) {
            case STATUS -> setStatus(event);
            case LIMIT -> setLimit(event);
            case BITRATE -> setBitrate(event);
            case NSFW -> setNsfw(event, tempRoomSettings);
            case CLAIM -> setOwnership(event);
            case NAME -> setName(event);
        }

    }
}
