package io.nexusbot.modules.commands.tempRoom;

import java.awt.Color;
import java.util.function.Consumer;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

@SlashCommands
public class TempRoomPermissions {
    private TempRoomService roomService = new TempRoomService();

    private boolean denyNotOwner(SlashCommandInteractionEvent event) {
        VoiceChannel voiceChannel = event.getChannel().asVoiceChannel();
        String denyMessage = "Используйте команду, находясь в своём голосовом канале.";
        if (voiceChannel == null) {
            EmbedUtil.replyEmbed(event, denyMessage, Color.RED);
            return true;
        }
        TempRoom room = roomService.get(voiceChannel.getIdLong());
        if (room == null || room.getOwnerId() != event.getMember().getIdLong()) {
            EmbedUtil.replyEmbed(event, denyMessage, Color.RED);
            return true;
        }
        return false;

    }
    // WARN: не забыть сохранять настройки комнаты после изменения.
    // Ивента, сохраняющего настройки канала автоматически, не будет.
    // Но можно сделать список игнорируемых разрешений, тогда сохранение от ивента
    // будет безопасным

    private PermissionOverrideAction getUpdateChannelPermissionAction(SlashCommandInteractionEvent event, Member member,
            Consumer<PermissionOverrideAction> action) {
        VoiceChannel room = event.getChannel().asVoiceChannel();
        PermissionOverrideAction overrideAction = room.upsertPermissionOverride(member);
        action.accept(overrideAction);
        return overrideAction;
    }

    @Command(description = "Команды для временных комнат")
    public void room(SlashCommandInteractionEvent event) {
    }

    @SubcommandGroup(parentName = "room", description = "Раздел с запретами")
    public void reject(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "room reject", description = "Запретить участнику подключение к голосовому каналу")
    public void connect(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Участник") Member member) {
        if (denyNotOwner(event)) {
            return;
        }
        getUpdateChannelPermissionAction(event, member, override -> override.setDenied(Permission.VOICE_CONNECT))
                .queue(_ -> EmbedUtil.replyEmbed(event,
                        member.getAsMention() + " больше не может подключиться к каналу", Color.GREEN),
                        error -> EmbedUtil.replyEmbed(event, "Не удалось запретить подключение к каналу для "
                                + member.getAsMention() + ".\nОшибка: " + error.getMessage(), Color.RED));
    }

    @SubcommandGroup(parentName = "room", description = "Раздел со снятием запретов")
    public void clear(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "room clear", name = "view", description = "Сбросить право на просмотр скрытого канала")
    public void clearView(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Участник") Member member) {
        if (denyNotOwner(event)) {
            return;
        }
        getUpdateChannelPermissionAction(event, member, override -> override.clear(Permission.VIEW_CHANNEL)).queue(
                _ -> EmbedUtil.replyEmbed(event,
                        member.getAsMention() + " больше не видит скрытый канал", Color.GREEN),
                error -> EmbedUtil.replyEmbed(event, "Не удалось запретить просмотр скрытого канала для "
                        + member.getAsMention() + ".\nОшибка: " + error.getMessage(), Color.RED));
    }

    @Subcommand(parentNames = "room clear", name = "connect", description = "Сбросить запрет на подключение к каналу")
    public void clearConnect(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Участник") Member member) {
        if (denyNotOwner(event)) {
            return;
        }
        getUpdateChannelPermissionAction(event, member, override -> override.clear(Permission.VIEW_CHANNEL)).queue(
                _ -> EmbedUtil.replyEmbed(event,
                        "право на подключение сброшено для " + member.getAsMention(), Color.GREEN),
                error -> EmbedUtil.replyEmbed(event, "Не удалось сбросить право на подключение участнику "
                        + member.getAsMention() + ".\nОшибка: " + error.getMessage(), Color.RED));
    }

    @SubcommandGroup(parentName = "room", description = "Разрешить или сбросить разрешение")
    public void accept(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "room accept", name = "view", description = "Разрешить участнику видеть скрытый канал")
    public void acceptView(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Участник, которому нужно разрешить видеть скрытый канал") Member member) {
        if (denyNotOwner(event)) {
            return;
        }
        getUpdateChannelPermissionAction(event, member, override -> override.setAllowed(Permission.VIEW_CHANNEL)).queue(
                _ -> EmbedUtil.replyEmbed(event,
                        member.getAsMention() + " теперь может видеть скрытый канал", Color.GREEN),
                error -> EmbedUtil.replyEmbed(event, "Не удалось разрешить видеть скрытый канал участнику "
                        + member.getAsMention() + ".\nОшибка: " + error.getMessage(), Color.RED));
    }

}
