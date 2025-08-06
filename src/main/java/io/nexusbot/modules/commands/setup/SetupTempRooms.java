package io.nexusbot.modules.commands.setup;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.r8zorg.jdatools.annotations.Choice;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.nexusbot.componentsData.ChannelMode;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.services.TempRoomCreatorService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class SetupTempRooms {
    private TempRoomCreatorService creatorService = new TempRoomCreatorService();

    @Subcommand(parentNames = "setup", description = "Команды для настройки бота")
    public void rooms(SlashCommandInteractionEvent event,
            @Option(name = "creator", description = "Канал-создатель", channelType = ChannelType.VOICE) VoiceChannel voiceChannelCreator,
            @Option(name = "category", description = "Категория для нового канала", channelType = ChannelType.CATEGORY) Category category,

            @Option(name = "limit", description = "Лимит пользователей в новом канале", required = false) Integer userLimit,
            @Option(name = "name", description = "Начальное название нового канала", required = false) String defalutChannelName,
            @Option(name = "mode", description = "Тип канала. Кастомный добавляет больше прав", required = false, choices = {
                    @Choice(name = "Обычный", value = ChannelMode.basic),
                    @Choice(name = "Кастомный", value = ChannelMode.custom) }) String channelMode,
            @Option(name = "role-needed", description = "Нужна ли роль для канала", required = false) Boolean isRoleNeeded,
            @Option(name = "empty-role-message", description = "Сообщение, которое отправит бот, если у участника нет нужной роли", required = false) String roleNotFoundMessage,
            @Option(name = "roles-ids", description = "ID ролей для доступа к каналу. Перечислить сразу все через пробел", required = false) String neededRolesIds,
            @Option(name = "log-channel", description = "Канал, в который будет отправляться информация о созданных ОБЫЧНЫХ каналах", required = false) TextChannel logChannel) {
        event.deferReply(true).queue();
        try {
            TempRoomCreator roomCreator = creatorService.getOrCreate(voiceChannelCreator.getIdLong());
            roomCreator.setTempRoomCategoryId(category.getIdLong());
            if (userLimit != null) {
                roomCreator.setUserLimit(userLimit);
            }
            if (defalutChannelName != null) {
                roomCreator.setDefaultTempChannelName(defalutChannelName);
            }
            if (channelMode != null) {
                roomCreator.setChannelMode(channelMode);
            }
            if (isRoleNeeded != null) {
                roomCreator.setRoleNeeded(isRoleNeeded);
            }
            if (roleNotFoundMessage != null) {
                roomCreator.setRoleNotFoundMessage(roleNotFoundMessage);
            }
            if (neededRolesIds != null) {
                try {
                List<Long> rolesIds = Arrays.stream(neededRolesIds.split("\\s+"))
                        .filter(s -> !s.isBlank())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                    roomCreator.setNeededRolesIds(rolesIds);
                } catch (NumberFormatException e) {
                    EmbedUtil.replyEmbed(event, "Не удалось считать айди ролей. Проверьте правильность ввода.", Color.RED);
                    return;
                }
            }
            if (logChannel != null) {
                roomCreator.setLogChannelId(logChannel.getIdLong());
            }
            creatorService.saveOrUpdate(roomCreator);
            EmbedUtil.replyEmbed(event.getHook(), "Сохранено.", Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event.getHook(), "Не удалось сохранить данные:" + e.getMessage(), Color.RED);
        }
    }
}
