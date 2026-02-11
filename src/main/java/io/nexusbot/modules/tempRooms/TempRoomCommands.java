package io.nexusbot.modules.tempRooms;

import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;

import io.github.r8zorg.jdatools.annotations.Choice;
import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import io.nexusbot.componentsData.DiscordConstants;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.utils.EmbedUtil;
import io.nexusbot.utils.TempRoomUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

@SlashCommands
public class TempRoomCommands {
    private TempRoomService roomService = new TempRoomService();

    private boolean denyNotVoiceChannel(SlashCommandInteractionEvent event) {
        if (event.getChannel().getType() != ChannelType.VOICE) {
            EmbedUtil.replyEmbed(event, "Вы можете использовать команду только в своём канале.", Color.RED);
            return true;
        }
        return false;
    }

    private boolean denyNotOwner(SlashCommandInteractionEvent event) {
        VoiceChannel voiceChannel = event.getChannel().asVoiceChannel();
        TempRoom room = roomService.get(voiceChannel.getIdLong());
        if (room == null || room.getOwnerId() != event.getMember().getIdLong()) {
            EmbedUtil.replyEmbed(event, "Используйте команду, находясь в своём голосовом канале.", Color.RED);
            return true;
        }
        return false;
    }

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

    @Subcommand(parentNames = "room", description = "Просмотреть список возможностей временной комнаты")
    public void help(SlashCommandInteractionEvent event) {
        String message = """
                - 1. В своей комнате Вы можете поменять:
                    - Статус
                    - Лимит
                    - Битрейт
                    - Вкл/выкл возрастное ограничение
                    - - В кастомном моде канала (настраивается администратором) Вы можете поменять ещё:
                        - Название канала
                - 2. Управление правами:
                    - Запретить/разрешить включать видео — запретить/отозвать право на включение видео и стрима всем, кроме Вас;
                    - Заблокировать/разблокировать доступ — запретить/отозвать право на подключение к каналу участнику (но не себе и не боту);
                    - Выгнать участника (через бота или интерфейс клиента)
                    - Закрыть/открыть комнату  — запретить/отозвать право на подключение к каналу всем, кроме Вас;
                    - Разрешить/запретить изменение статуса - разрешить/запретить другим участникам менять статус канала;
                    - - В кастомном моде канала (настраивается администратором) Вы можете ещё:
                        - Скрыть/показать комнату — запретить/отозвать право на просмотр комнаты всем, кроме Вас;
                        - Разрешить видеть скрытый канал — белый список участников, которые смогут видеть и подключаться (если они не заблокированы) к скрытому каналу;
                        - Запретить видеть скрытый канал — отозвать право на просмотр скрытого канала участнику;
                """;
        EmbedUtil.replyEmbed(event, message, Color.WHITE);
    }

    @SubcommandGroup(parentName = "room", description = "Раздел с запретами")
    public void reject(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "room reject", description = "Запретить участнику подключение к голосовому каналу")
    public void connect(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Участник") Member member) {
        if (denyNotVoiceChannel(event) || denyNotOwner(event)) {
            return;
        }

        getUpdateChannelPermissionAction(event, member, override -> override.setDenied(Permission.VOICE_CONNECT)).queue(
                override -> {
                    TempRoomUtil.saveOverrides(event.getMember().getIdLong(), override);
                    EmbedUtil.replyEmbed(event,
                            member.getAsMention() + " больше не может подключиться к каналу", Color.GREEN);
                },
                error -> EmbedUtil.replyEmbed(event, "Не удалось запретить подключение к каналу для "
                        + member.getAsMention() + ".\nОшибка: " + error.getMessage(), Color.RED));
    }

    @SubcommandGroup(parentName = "room", description = "Раздел со снятием запретов")
    public void clear(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "room clear", name = "view", description = "Сбросить право на просмотр скрытого канала")
    public void clearView(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Участник") Member member) {
        if (denyNotVoiceChannel(event) || denyNotOwner(event)) {
            return;
        }

        getUpdateChannelPermissionAction(event, member, override -> override.clear(Permission.VIEW_CHANNEL)).queue(
                override -> {
                    TempRoomUtil.saveOverrides(event.getMember().getIdLong(), override);
                    EmbedUtil.replyEmbed(event,
                            member.getAsMention() + " больше не видит скрытый канал", Color.GREEN);
                },
                error -> EmbedUtil.replyEmbed(event, "Не удалось запретить просмотр скрытого канала для "
                        + member.getAsMention() + ".\nОшибка: " + error.getMessage(), Color.RED));
    }

    @Subcommand(parentNames = "room clear", name = "connect", description = "Сбросить запрет на подключение к каналу")
    public void clearConnect(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Участник") Member member) {
        if (denyNotVoiceChannel(event) || denyNotOwner(event)) {
            return;
        }

        getUpdateChannelPermissionAction(event, member, override -> override.clear(Permission.VIEW_CHANNEL)).queue(
                override -> {
                    TempRoomUtil.saveOverrides(event.getMember().getIdLong(), override);
                    EmbedUtil.replyEmbed(event,
                            "право на подключение сброшено для " + member.getAsMention(), Color.GREEN);
                },
                error -> EmbedUtil.replyEmbed(event, "Не удалось сбросить право на подключение участнику "
                        + member.getAsMention() + ".\nОшибка: " + error.getMessage(), Color.RED));
    }

    @SubcommandGroup(parentName = "room", description = "Разрешить или сбросить разрешение")
    public void accept(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "room accept", name = "view", description = "Разрешить участнику видеть скрытый канал")
    public void acceptView(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Участник, которому нужно разрешить видеть скрытый канал") Member member) {
        if (denyNotVoiceChannel(event) || denyNotOwner(event)) {
            return;
        }

        getUpdateChannelPermissionAction(event, member, override -> override.grant(Permission.VIEW_CHANNEL)).queue(
                override -> {
                    TempRoomUtil.saveOverrides(event.getMember().getIdLong(), override);
                    EmbedUtil.replyEmbed(event,
                            member.getAsMention() + " теперь может видеть скрытый канал", Color.GREEN);
                },
                error -> EmbedUtil.replyEmbed(event, "Не удалось разрешить видеть скрытый канал участнику "
                        + member.getAsMention() + ".\nОшибка: " + error.getMessage(), Color.RED));
    }

    @Subcommand(parentNames = "room", description = "Выгнать участника из своей комнаты")
    public void disconnect(SlashCommandInteractionEvent event,
            @Option(name = "member", description = "Участник") Member member) {
        if (denyNotVoiceChannel(event) || denyNotOwner(event)) {
            return;
        }
        if (event.getMember().getIdLong() == member.getIdLong()) {
            EmbedUtil.replyEmbed(event, "Вы не можете выгнать себя.", Color.RED);
            return;
        }

        if (!event.getChannel().asVoiceChannel().getMembers().contains(member)) {
            EmbedUtil.replyEmbed(event, "Участник не в Вашем канале.", Color.RED);
            return;
        }
        event.getGuild().moveVoiceMember(member, null).queue(
                success -> EmbedUtil.replyEmbed(event, "Участник" + member.getAsMention() + " выгнан", Color.GREEN),
                error -> EmbedUtil.replyEmbed(event, "Не удалось выгнать участника: " + error.getMessage(), Color.RED));
    }

    @Subcommand(parentNames = "room", description = "Поменять регион голосового канала")
    public void region(SlashCommandInteractionEvent event,
            @Option(name = "region", description = "Регион", choices = {
                    @Choice(name = "Automatic", value = "automatic"),
                    @Choice(name = "Brazil", value = "brazil"),
                    @Choice(name = "Hong Kong", value = "hongkong"),
                    @Choice(name = "India", value = "india"),
                    @Choice(name = "Japan", value = "japan"),
                    @Choice(name = "Rotterdam", value = "rotterdam"),
                    @Choice(name = "Singapore", value = "singapore"),
                    @Choice(name = "South Korea", value = "south-korea"),
                    @Choice(name = "South Africa", value = "southafrica"),
                    @Choice(name = "Sydney", value = "sydney"),
                    @Choice(name = "US Central", value = "us-central"),
                    @Choice(name = "US East", value = "us-east"),
                    @Choice(name = "US South", value = "us-south"),
                    @Choice(name = "US West", value = "us-west")
            }) String selectedRegion) {
        if (denyNotVoiceChannel(event) || denyNotOwner(event)) {
            return;
        }
        VoiceChannel voiceChannel = event.getChannel().asVoiceChannel();
        Region region = DiscordConstants.REGIONS
                .stream()
                .filter(_region -> _region.getKey().equals(selectedRegion))
                .findFirst()
                .orElseThrow();
        voiceChannel.getManager().setRegion(region).queue(
                success -> EmbedUtil.replyEmbed(event, "Регион изменён на: " + region.getName(), Color.GREEN),
                error -> EmbedUtil.replyEmbed(event, "Не удалось сменить регион: " + error.getMessage(), Color.RED));
    }

    // @SubcommandGroup(parentName = "room", description = "Получить список
    // участников")
    public void get(SlashCommandInteractionEvent event) {
    }

    // @Subcommand(parentNames = "room get", description = "Получить участников,
    // заблокированных в канале")
    public void blocked(SlashCommandInteractionEvent event) {
        if (denyNotVoiceChannel(event) || denyNotOwner(event)) {
            return;
        }

        VoiceChannel voiceChannel = event.getChannel().asVoiceChannel();
        List<Member> blockedMembers = voiceChannel.getPermissionOverrides().stream()
                .filter(override -> override.getDenied().contains(Permission.VOICE_CONNECT))
                .map(PermissionOverride::getMember)
                .toList();
        if (blockedMembers.isEmpty()) {
            EmbedUtil.replyEmbed(event, "В канале нет заблокированных участников", Color.WHITE);
            return;
        }

        String message = "Список заблокированных участников:\n";
        // TODO: MembersUtil.loadMembers()
        for (Member member : blockedMembers) {
            message += member.getAsMention() + "\n";
        }
        EmbedUtil.replyEmbed(event, message, Color.WHITE);
    }

}
