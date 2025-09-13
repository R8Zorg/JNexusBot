package io.nexusbot.modules.listeners.tempRooms.selectMenu;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.componentsData.TempRoomPermissionsMenu;
import io.nexusbot.utils.EmbedUtil;
import io.nexusbot.utils.MembersUtil;
import io.nexusbot.utils.TempRoomUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

@EventListeners
public class OnRoomPermissionsSubMenuSelect extends ListenerAdapter {
    private final Map<String, Consumer<StringSelectInteractionEvent>> stringMenuHandler = new HashMap<>();
    private final Map<String, Consumer<EntitySelectInteractionEvent>> entityMenuHandler = new HashMap<>();

    public OnRoomPermissionsSubMenuSelect() {
        stringMenuHandler.put(TempRoomPermissionsMenu.CLEAR_CONNECT.getValue(), this::clearConnect);
        stringMenuHandler.put(TempRoomPermissionsMenu.KICK.getValue(), this::kickMembers);
        stringMenuHandler.put(TempRoomPermissionsMenu.REJECT_VIEW_CHANNEL.getValue(), this::clearViewChannel);

        entityMenuHandler.put(TempRoomPermissionsMenu.REJECT_CONNECT.getValue(), this::rejectConnect);
        entityMenuHandler.put(TempRoomPermissionsMenu.PERMIT_VIEW_CHANNEL.getValue(), this::permitViewChannel);
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        Consumer<StringSelectInteractionEvent> handler = stringMenuHandler.get(event.getComponentId());
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        Consumer<EntitySelectInteractionEvent> handler = entityMenuHandler.get(event.getComponentId());
        if (handler != null) {
            handler.accept(event);
        }
    }

    private CompletableFuture<List<Member>> getSelectedMembers(GenericSelectMenuInteractionEvent<?, ?> event) {
        if (event instanceof StringSelectInteractionEvent stringEvent) {
            List<Long> selectedMemberIds = stringEvent.getSelectedOptions()
                    .stream()
                    .map(option -> Long.parseLong(option.getValue()))
                    .toList();
            return MembersUtil.loadMembers(event, selectedMemberIds);
        } else if (event instanceof EntitySelectInteractionEvent entityEvent) {
            return CompletableFuture.completedFuture(
                    entityEvent.getMentions().getMembers().stream().toList());
        } else {
            throw new IllegalArgumentException("Неподдерживаемый тип события: " + event.getClass());
        }
    }

    private void changePermissions(GenericSelectMenuInteractionEvent<?, ?> event, List<Member> members,
            Consumer<PermissionOverrideAction> permissionOverride,
            Runnable extraAction) {
        event.deferEdit().queue();
        if (event.getValues().get(0).equals(GlobalIds.NOTHING.getValue())) {
            return;
        }

        VoiceChannel voiceChannel = event.getChannel().asVoiceChannel();

        List<PermissionOverrideAction> restActions = members.stream()
                .filter(member -> member.getIdLong() != event.getJDA().getSelfUser().getIdLong())
                .filter(member -> member.getIdLong() != event.getMember().getIdLong())
                .map(member -> {
                    PermissionOverrideAction permissionOverrideAction = voiceChannel.upsertPermissionOverride(member);
                    permissionOverride.accept(permissionOverrideAction);
                    return permissionOverrideAction;
                })
                .toList();
        if (restActions.isEmpty()) {
            EmbedUtil.replyEmbed(event.getHook(), "Вы не можете выбрать себя или бота", Color.RED);
            return;
        }

        RestAction.allOf(restActions).queue(
                permissionOverrides -> {
                    TempRoomUtil.saveOverrides(event.getMember().getIdLong(), permissionOverrides);
                    if (extraAction == null) {
                        EmbedUtil.replyEmbed(event.getHook(), "Права обновлены.", Color.GREEN);
                    } else {
                        extraAction.run();
                    }
                }, error -> {
                    EmbedUtil.replyEmbed(event.getHook(),
                            "Не удалось сохранить одно или несколько прав: " + error.getMessage(),
                            Color.RED);
                });
    }

    private void clearConnect(StringSelectInteractionEvent event) {
        List<Long> memberIds = event.getChannel().asVoiceChannel().getPermissionOverrides()
                .stream()
                .filter(po -> po.getDenied().contains(Permission.VOICE_CONNECT))
                .filter(PermissionOverride::isMemberOverride)
                .map(po -> po.getIdLong())
                .toList();
        MembersUtil.loadMembers(event, memberIds).thenAccept(members -> changePermissions(event, members,
                overrideAction -> overrideAction.clear(Permission.VOICE_CONNECT), null));
    }

    private List<RestAction<Void>> getKickVoiceMembersAction(GenericSelectMenuInteractionEvent<?, ?> event,
            List<Member> members) {
        return members.stream()
                .filter(member -> event.getChannel().asVoiceChannel().getMembers().contains(member))
                .map(member -> event.getGuild().moveVoiceMember(member, null))
                .toList();
    }

    private void kickVoiceMembers(GenericSelectMenuInteractionEvent<?, ?> event, List<Member> members,
            String onSuccessMessage,
            String onErrorMessage,
            String onEmptyListMessage) {
        List<RestAction<Void>> kickVoiceMembersAction = getKickVoiceMembersAction(event, members);
        if (kickVoiceMembersAction.isEmpty()) {
            EmbedUtil.replyEmbed(event.getHook(), onEmptyListMessage, Color.GREEN);
            return;
        }
        RestAction.allOf(kickVoiceMembersAction).queue(_ -> {
            EmbedUtil.replyEmbed(event.getHook(),
                    onSuccessMessage,
                    Color.GREEN);
        }, error -> {
            EmbedUtil.replyEmbed(event.getHook(),
                    onErrorMessage + error.getMessage(),
                    Color.RED);
        });
    }

    private void kickMembers(StringSelectInteractionEvent event) {
        event.deferEdit().queue();
        getSelectedMembers(event).thenAccept(members -> kickVoiceMembers(event, members,
                "Все участники выгнаны.",
                "Возникла ошибка при исключении одного или нескольких участников: ",
                ""));
    }

    private void permitViewChannel(EntitySelectInteractionEvent event) {
        getSelectedMembers(event).thenAccept(members -> changePermissions(event, members,
                overrideAction -> overrideAction.grant(Permission.VIEW_CHANNEL), null));
    }

    private void clearViewChannel(StringSelectInteractionEvent event) {
        getSelectedMembers(event).thenAccept(members -> {
            changePermissions(event, members,
                    overrideAction -> overrideAction.clear(Permission.VIEW_CHANNEL),
                    null);
        });
    }

    private void rejectConnect(EntitySelectInteractionEvent event) {
        getSelectedMembers(event).thenAccept(members -> {
            changePermissions(event, members,
                    overrideAction -> overrideAction.deny(Permission.VOICE_CONNECT),
                    () -> kickVoiceMembers(event, members,
                            "Выбранные участники исключены и больше не смогут присоединиться к каналу.",
                            "Произошла ошибка при попытке исключить одного или нескольких участников из канала: ",
                            "Выбранные участники больше не смогут присоединиться к каналу."));
        });
    }

}
