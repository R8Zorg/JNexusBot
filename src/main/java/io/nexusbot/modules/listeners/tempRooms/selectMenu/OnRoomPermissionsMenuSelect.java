package io.nexusbot.modules.listeners.tempRooms.selectMenu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.DiscordConstants;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.componentsData.TempRoomPermissionsMenu;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.utils.EmbedUtil;
import io.nexusbot.utils.MembersUtil;
import io.nexusbot.utils.TempRoomUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.Builder;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

@EventListeners
public class OnRoomPermissionsMenuSelect extends ListenerAdapter {
    private TempRoomService tempRoomService = new TempRoomService();

    private void lockRoom(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .deny(Permission.VOICE_CONNECT).queue(_ -> {
                    // TempRoomUtil.saveOverrides(event.getMember().getIdLong(), action);
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Канал закрыт", Color.GREEN);
                });
    }

    private void unlockRoom(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .clear(Permission.VOICE_CONNECT).queue(_ -> {
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Канал открыт", Color.GREEN);
                });
    }

    private StringSelectMenu getMembersMenu(GenericSelectMenuInteractionEvent<?, ?> event,
            List<Member> members, String menuId) {
        Member owner = event.getGuild().getMember(event.getMember());
        Member bot = event.getGuild().getMember(event.getJDA().getSelfUser());
        members.remove(owner);
        members.remove(bot);
        Builder selectMenuBuilder = StringSelectMenu.create(menuId)
                .setPlaceholder("Выберите участников")
                .setMaxValues(DiscordConstants.MAX_SELECT_MENU_ITEMS.getValue());
        for (Member member : members) {
            selectMenuBuilder.addOption(member.getEffectiveName(), member.getId());
            if (selectMenuBuilder.getOptions().size() >= DiscordConstants.MAX_SELECT_MENU_ITEMS.getValue()) {
                break;
            }
        }
        return selectMenuBuilder.build();
    }

    private void handleMembersWithPermission(GenericSelectMenuInteractionEvent<?, ?> event, Permission permission,
            Function<PermissionOverride, EnumSet<Permission>> extractor, Consumer<List<Member>> onCompleteAction,
            String emptyMessage) {
        VoiceChannel voiceChannel = event.getChannel().asVoiceChannel();
        List<Long> memberIds = voiceChannel.getPermissionOverrides()
                .stream()
                .filter(override -> override.isMemberOverride())
                .filter(override -> extractor.apply(override).contains(permission))
                .map(override -> override.getIdLong())
                .filter(id -> id != event.getMember().getIdLong() && id != event.getJDA().getSelfUser().getIdLong())
                .toList();

        if (memberIds.isEmpty()) {
            EmbedUtil.replyEmbed(event, emptyMessage, Color.GREEN);
            return;
        }

        MembersUtil.loadMembers(event, memberIds)
                .thenAccept(members -> onCompleteAction.accept(members));
    }

    private void sendClearConnectMembersMenu(StringSelectInteractionEvent event, List<Member> rejectedMembers) {
        if (rejectedMembers.isEmpty()) {
            EmbedUtil.replyEmbed(event, "Не удалось получить заблокированных участников", Color.RED);
            return;
        }
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, с которых хотите снять запрет на вход в канал.
                """, Color.GREEN);

        event.replyEmbeds(embed)
                .addActionRow(getMembersMenu(event, rejectedMembers,
                        TempRoomPermissionsMenu.CLEAR_CONNECT.getValue()))
                .setEphemeral(true)
                .queue();

    }

    private void sendRejectedViewChannelMembersMenu(StringSelectInteractionEvent event, List<Member> acceptedMembers) {
        if (acceptedMembers.isEmpty()) {
            EmbedUtil.replyEmbed(event, "В канале нет пользователей, которые могут просматривать скрытый канал",
                    Color.GREEN);
            return;
        }
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, которому хотите сброить доступ к закрытому каналу.
                """, Color.GREEN);

        event.replyEmbeds(embed)
                .addActionRow(
                        getMembersMenu(event, acceptedMembers, TempRoomPermissionsMenu.REJECT_VIEW_CHANNEL.getValue()))
                .setEphemeral(true)
                .queue();
    }

    private void rejectMemberConnect(StringSelectInteractionEvent event) {
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, которому хотите запретить вход в канал.
                Если участника нет в списке, воспользуйтесь слеш командной `/room reject connect`
                """, Color.GREEN);

        event.replyEmbeds(embed)
                .addActionRow(EntitySelectMenu.create(
                        TempRoomPermissionsMenu.REJECT_CONNECT.getValue(), SelectTarget.USER)
                        .setPlaceholder("Выберите участника")
                        .setMaxValues(DiscordConstants.MAX_SELECT_MENU_ITEMS.getValue())
                        .build())
                .setEphemeral(true)
                .queue();
    }

    private void clearMemberConnect(StringSelectInteractionEvent event) {
        handleMembersWithPermission(event, Permission.VOICE_CONNECT, PermissionOverride::getDenied,
                members -> sendClearConnectMembersMenu(event, members), "В канале нет заблокированных пользователей");
    }

    private void kickMember(StringSelectInteractionEvent event, long ownerId) {
        List<Member> members = new ArrayList<>(event.getChannel().asVoiceChannel().getMembers());
        if (members.isEmpty()) {
            EmbedUtil.replyEmbed(event, "В канале никого, кроме Вас", Color.RED);
            return;
        }
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, которого хотите выгнать из канала.
                Вы также можете использовать слеш команду `/room disconnect`
                """, Color.GREEN);

        event.replyEmbeds(embed)
                .addActionRow(getMembersMenu(event, members, TempRoomPermissionsMenu.KICK.getValue()))
                .setEphemeral(true)
                .queue();
    }

    private void ghostRoom(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .deny(Permission.VIEW_CHANNEL).queue(_ -> {
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Канал скрыт", Color.GREEN);
                });
    }

    private void unghostRoom(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .clear(Permission.VIEW_CHANNEL).queue(_ -> {
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Канал явлен", Color.GREEN);
                });
    }

    private void permitViewChannel(StringSelectInteractionEvent event) {
        MessageEmbed embed = EmbedUtil.generateEmbed("""
                Выберите участников, которые будет видеть скрытый канал.
                Если участника нет в списке, воспользуйтесь слеш командной `/room accept view`
                """, Color.GREEN);

        event.replyEmbeds(embed)
                .addActionRow(EntitySelectMenu.create(
                        TempRoomPermissionsMenu.PERMIT_VIEW_CHANNEL.getValue(), SelectTarget.USER)
                        .setMaxValues(DiscordConstants.MAX_SELECT_MENU_ITEMS.getValue())
                        .setPlaceholder("Выберите участника(ов)")
                        .build())
                .setEphemeral(true)
                .queue();
    }

    private void clearViewChannel(StringSelectInteractionEvent event) {
        handleMembersWithPermission(event, Permission.VIEW_CHANNEL, PermissionOverride::getAllowed,
                members -> sendRejectedViewChannelMembersMenu(event, members),
                "В канале нет заблокированных пользователей");
    }

    private void rejectStream(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .deny(Permission.VOICE_STREAM).queue(_ -> {
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Право на включение стрима и вебкамеры отключено", Color.GREEN);
                });
    }

    private void clearStream(StringSelectInteractionEvent event) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        room.upsertPermissionOverride(event.getGuild().getPublicRole())
                .clear(Permission.VOICE_STREAM).queue(_ -> {
                    TempRoomUtil.saveOverrides(room, event.getMember().getIdLong());
                    EmbedUtil.replyEmbed(event, "Право на включение стрима и вебкамеры сброшено", Color.GREEN);
                });
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

        switch (TempRoomPermissionsMenu.fromValue(selectedOptionId)) {
            case LOCK -> lockRoom(event);
            case UNLOCK -> unlockRoom(event);
            case REJECT_CONNECT -> rejectMemberConnect(event);
            case CLEAR_CONNECT -> clearMemberConnect(event);
            case KICK -> kickMember(event, ownerId);
            case REJECT_STREAM -> rejectStream(event);
            case CLEAR_STREAM -> clearStream(event);
            case GHOST -> ghostRoom(event);
            case UNGHOST -> unghostRoom(event);
            case PERMIT_VIEW_CHANNEL -> permitViewChannel(event);
            case REJECT_VIEW_CHANNEL -> clearViewChannel(event);
        }
    }
}
