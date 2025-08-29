package io.nexusbot.modules.listeners.tempRooms;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.DiscordConstants;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.componentsData.TempRoomPermissionsMenu;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.database.services.TempRoomSettingsService;
import io.nexusbot.utils.EmbedUtil;
import io.nexusbot.utils.TempRoomUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.Builder;

@EventListeners
public class OnRoomPermissionsMenuSelect extends ListenerAdapter {
    private TempRoomService tempRoomService = new TempRoomService();
    private TempRoomSettingsService tempRoomSettingsService = new TempRoomSettingsService();

    private void lockRoom(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .setDenied(Permission.VOICE_CONNECT).queue(_ -> {
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Канал закрыт", Color.WHITE);
                });
    }

    private void unlockRoom(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .clear(Permission.VOICE_CONNECT).queue(_ -> {
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Канал открыт", Color.WHITE);
                });
    }

    private void rejectMember(StringSelectInteractionEvent event) {
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, которому хотите запретить вход в канал.
                Если участника нет в списке, воспользуйтесь слеш командной `/room reject`
                """, Color.WHITE);

        event.replyEmbeds(embed)
                .addActionRow(EntitySelectMenu.create(
                        TempRoomPermissionsMenu.REJECT.getValue(), SelectTarget.USER)
                        .setPlaceholder("Выберите участника")
                        .setMaxValues(DiscordConstants.MAX_SELECT_MENU_ITEMS.getValue())
                        .build())
                .setEphemeral(true)
                .queue();
    }

    private StringSelectMenu getMembersMenu(List<Member> members, String menuId) {
        int membersAmount = DiscordConstants.MAX_SELECT_MENU_ITEMS.getValue() - 1;
        Builder selectMenuBuilder = StringSelectMenu.create(menuId)
                .setPlaceholder("Выберите участников")
                .setMaxValues(membersAmount)
                .addOption("Никого не выбирать", GlobalIds.NOTHING.getValue());
        for (Member member : members) {
            selectMenuBuilder.addOption(member.getEffectiveName(), member.getId());
            if (selectMenuBuilder.getOptions().size() >= membersAmount) {
                break;
            }
        }
        return selectMenuBuilder.build();
    }

    private void permitMember(StringSelectInteractionEvent event) {
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, с которого хотите снять запрет на вход в канал.
                """, Color.WHITE);
        List<Member> rejectedMembers = event.getChannel().asVoiceChannel().getPermissionOverrides()
                .stream()
                .filter(override -> override.isMemberOverride())
                .filter(override -> override.getDenied().contains(Permission.VOICE_CONNECT))
                .map(override -> override.getMember())
                .collect(Collectors.toList());
        if (rejectedMembers.isEmpty()) {
            EmbedUtil.replyEmbed(event, "В канале нет заблокированных пользователей", Color.WHITE);
            return;
        }

        event.replyEmbeds(embed)
                .addActionRow(getMembersMenu(rejectedMembers, TempRoomPermissionsMenu.PERMIT.getValue()))
                .setEphemeral(true)
                .queue();
    }

    private void kickMember(StringSelectInteractionEvent event) {
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, которого хотите выгнать из канала.
                Вы также можете использовать слеш команду `/room disconnect`
                """, Color.WHITE);

        List<Member> members = event.getChannel().asVoiceChannel().getMembers();
        event.replyEmbeds(embed)
                .addActionRow(getMembersMenu(members, TempRoomPermissionsMenu.KICK.getValue()))
                .setEphemeral(true)
                .queue();
    }

    private void ghostRoom(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .setDenied(Permission.VIEW_CHANNEL).queue(_ -> {
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Канал скрыт", Color.WHITE);
                });
    }

    private void unghostRoom(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .clear(Permission.VIEW_CHANNEL).queue(_ -> {
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Канал явлен", Color.WHITE);
                });
    }

    private void acceptMember(StringSelectInteractionEvent event) {
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, который будет видеть скрытый канал.
                Если участника нет в списке, воспользуйтесь слеш командной `/room accept`
                """, Color.WHITE);

        event.replyEmbeds(embed)
                .addActionRow(EntitySelectMenu.create(
                        TempRoomPermissionsMenu.ACCEPT.getValue(), SelectTarget.USER)
                        .setMaxValues(DiscordConstants.MAX_SELECT_MENU_ITEMS.getValue())
                        .setPlaceholder("Выберите участника")
                        .build())
                .setEphemeral(true)
                .queue();
    }

    private void denyMember(StringSelectInteractionEvent event) {
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, которому хотите сброить доступ к закрытому каналу.
                """, Color.WHITE);
        List<Member> acceptedMembers = event.getChannel().asVoiceChannel().getPermissionOverrides()
                .stream()
                .filter(override -> override.isMemberOverride())
                .filter(override -> override.getAllowed().contains(Permission.VIEW_CHANNEL))
                .map(override -> override.getMember())
                .collect(Collectors.toList());
        if (acceptedMembers.isEmpty()) {
            EmbedUtil.replyEmbed(event, "В канале нет пользователей, которые могут просматривать скрытый канал",
                    Color.WHITE);
            return;
        }

        event.replyEmbeds(embed)
                .addActionRow(getMembersMenu(acceptedMembers, TempRoomPermissionsMenu.DENY.getValue()))
                .setEphemeral(true)
                .queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals(TempRoomPermissionsMenu.ID)) {
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

        TempRoomSettings tempRoomSettings = tempRoomSettingsService.get(ownerId, event.getGuild().getIdLong());
        switch (TempRoomPermissionsMenu.fromValue(selectedOptionId)) {
            case LOCK -> lockRoom(event);
            case UNLOCK -> unlockRoom(event);
            case REJECT -> rejectMember(event);
            case PERMIT -> permitMember(event);
            case KICK -> kickMember(event);
            case GHOST -> ghostRoom(event);
            case UNGHOST -> unghostRoom(event);
            case ACCEPT -> acceptMember(event);
            case DENY -> denyMember(event);
        }
    }
}
