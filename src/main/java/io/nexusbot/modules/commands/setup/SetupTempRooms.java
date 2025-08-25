package io.nexusbot.modules.commands.setup;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.github.r8zorg.jdatools.annotations.Choice;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import io.nexusbot.componentsData.ChannelMode;
import io.nexusbot.database.entities.TempRoomCreator;
import io.nexusbot.database.services.TempRoomCreatorService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

@SlashCommands
public class SetupTempRooms {
    private TempRoomCreatorService creatorService = new TempRoomCreatorService();

    @SubcommandGroup(parentName = "setup")
    public void rooms(SlashCommandInteractionEvent event) {
    }

    private void saveRoomCreator(long roomCreatorId, long categoryId, String roleNotFoundMessage,
            TextChannel infoChannel) {
        TempRoomCreator roomCreator = new TempRoomCreator(roomCreatorId);
        roomCreator.setTempRoomCategoryId(categoryId);
        if (roleNotFoundMessage != null) {
            roomCreator.setRoleNotFoundMessage(roleNotFoundMessage);
        }
        if (infoChannel != null) {
            roomCreator.setLogChannelId(infoChannel.getIdLong());
        }
        creatorService.saveOrUpdate(roomCreator);
    }

    private void createAndSaveRoomCreators(Category creatorsCategory, Category tempRoomsCategory, Integer creatorsCount,
            String limits, String roleNotFoundMessage, TextChannel infoChannel,
            InteractionHook hook) {
        String roomName = "【➕】Создать";
        int[] limitsList = new int[0];
        if (limits != null) {
            limitsList = Arrays.stream(limits.split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }

        for (int i = 1; i <= creatorsCount; i++) {
            int userLimit = 0;
            if (i <= limitsList.length) {
                userLimit = limitsList[i];
                roomName += " (" + userLimit + ")";
            }
            try {
                creatorsCategory.createVoiceChannel(roomName)
                        .setUserlimit(userLimit)
                        .queue(voiceChannel -> {
                            saveRoomCreator(voiceChannel.getIdLong(), tempRoomsCategory.getIdLong(),
                                    roleNotFoundMessage,
                                    infoChannel);
                        });
            } catch (Exception e) {
                EmbedUtil.replyEmbed(hook, "Не удалось создать голосовой канал" + e.getMessage(), Color.RED);
            }
        }
    }

    @Subcommand(parentNames = "setup rooms", description = "Создать канал/каналы и категорию")
    public void add(SlashCommandInteractionEvent event,
            @Option(name = "count", description = "Количество каналов-создателей, которые будут созданы в новой категории 'Создать комнату'") Integer creatorsCount,
            @Option(name = "creators-category-name", description = "Название категории, в которой будут каналы-создатели") String creatorsCategoryName,
            @Option(name = "category-name", description = "Название категории, в которой будут создаваться временные комнаты") String tempRoomsCategoryName,
            @Option(name = "limits", description = "Лимиты через пробел для каждого канала соответственно. 0 по умолчанию", required = false) String limits,
            @Option(name = "empty-role-message", description = "Сообщение, которое отправит бот, если у участника нет нужной роли", required = false) String roleNotFoundMessage,
            @Option(name = "info-channel", description = "Канал, в который будет отправляться информация о созданных ОБЫЧНЫХ каналах", required = false) TextChannel infoChannel) {
        if (creatorsCount < 1) {
            EmbedUtil.replyEmbed(event, "Создать отрицательное количество каналов? Серьёзно? Может, удалить "
                    + Math.abs(creatorsCount) + " каналов на сервере?", Color.RED);
            return;
        }
        int maxChannelsInARow = 6;
        if (creatorsCount > maxChannelsInARow) {
            EmbedUtil.replyEmbed(event,
                    "Можно создать не больше " + maxChannelsInARow + " каналов-создателей за раз.", Color.RED);
            return;
        }
        event.deferReply(true).queue();
        try {
            event.getGuild().createCategory(creatorsCategoryName).queue(creatorsCategory -> {
                event.getGuild().createCategory(tempRoomsCategoryName).queue(tempRoomsCategory -> {
                    createAndSaveRoomCreators(creatorsCategory, tempRoomsCategory, creatorsCount, limits,
                            roleNotFoundMessage, infoChannel,
                            event.getHook());
                    EmbedUtil.replyEmbed(event.getHook(), "Сохранено.", Color.GREEN);
                });
            });
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event.getHook(), "Не удалось создать категорию" + e.getMessage(), Color.RED);
        }

    }

    @Subcommand(parentNames = "setup rooms", description = "Добавить канал или перезаписать настройки существующего")
    public void save(SlashCommandInteractionEvent event,
            @Option(name = "creator", description = "Канал-создатель", channelType = ChannelType.VOICE) VoiceChannel roomCreator,
            @Option(name = "category", description = "Категория для нового канала", channelType = ChannelType.CATEGORY) Category category,

            @Option(name = "limit", description = "Лимит пользователей в новом канале", required = false) Integer userLimit,
            @Option(name = "name", description = "Начальное название нового канала", required = false) String defalutChannelName,
            @Option(name = "mode", description = "Тип канала. Кастомный добавляет больше прав", required = false, choices = {
                    @Choice(name = "Обычный", value = ChannelMode.basic),
                    @Choice(name = "Кастомный", value = ChannelMode.custom) }) String channelMode,
            @Option(name = "role-needed", description = "Нужна ли роль для канала", required = false) Boolean isRoleNeeded,
            @Option(name = "empty-role-message", description = "Сообщение, которое отправит бот, если у участника нет нужной роли", required = false) String roleNotFoundMessage,
            @Option(name = "roles-ids", description = "ID ролей для доступа к каналу. Перечислить сразу все через пробел", required = false) String neededRolesIds,
            @Option(name = "info-channel", description = "Канал, в который будет отправляться информация о созданных ОБЫЧНЫХ каналах", required = false) TextChannel infoChannel) {
        event.deferReply(true).queue();
        try {
            TempRoomCreator tempRoomCreator = creatorService.getOrCreate(roomCreator.getIdLong());
            tempRoomCreator.setTempRoomCategoryId(category.getIdLong());
            if (userLimit != null) {
                tempRoomCreator.setUserLimit(userLimit);
            }
            if (defalutChannelName != null) {
                tempRoomCreator.setDefaultTempChannelName(defalutChannelName);
            }
            if (channelMode != null) {
                tempRoomCreator.setChannelMode(channelMode);
            }
            if (isRoleNeeded != null) {
                tempRoomCreator.setRoleNeeded(isRoleNeeded);
            }
            if (roleNotFoundMessage != null) {
                tempRoomCreator.setRoleNotFoundMessage(roleNotFoundMessage);
            }
            if (neededRolesIds != null) {
                try {
                    List<Long> rolesIds = Arrays.stream(neededRolesIds.split("\\s+"))
                            .filter(s -> !s.isBlank())
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                    tempRoomCreator.setNeededRolesIds(rolesIds);
                } catch (NumberFormatException e) {
                    EmbedUtil.replyEmbed(event, "Не удалось считать айди ролей. Проверьте правильность ввода.",
                            Color.RED);
                    return;
                }
            }
            if (infoChannel != null) {
                tempRoomCreator.setLogChannelId(infoChannel.getIdLong());
            }
            creatorService.saveOrUpdate(tempRoomCreator);
            EmbedUtil.replyEmbed(event.getHook(), "Сохранено.", Color.GREEN);
        } catch (Exception e) {
            EmbedUtil.replyEmbed(event.getHook(), "Не удалось сохранить данные:" + e.getMessage(), Color.RED);
        }
    }

    private void resetSettings(TempRoomCreator tempRoomCreator, VoiceChannel roomCreator, String all,
            String userLimit, String defalutChannelName, String channelMode,
            String isRoleNeeded, String roleNotFoundMessage, String neededRolesIds, String infoChannel) {
        if (all != null) {
            tempRoomCreator.setUserLimit(0);
            tempRoomCreator.setDefaultTempChannelName(null);
            tempRoomCreator.setChannelMode(ChannelMode.basic);
            tempRoomCreator.setRoleNeeded(false);
            tempRoomCreator.setRoleNotFoundMessage(null);
            tempRoomCreator.setLogChannelId(null);
            creatorService.saveOrUpdate(tempRoomCreator);
            creatorService.setNeededRolesIds(roomCreator.getIdLong(), Collections.emptyList());
            return;
        }
        if (userLimit != null) {
            tempRoomCreator.setUserLimit(0);
        }
        if (defalutChannelName != null) {
            tempRoomCreator.setDefaultTempChannelName(null);
        }
        if (channelMode != null) {
            tempRoomCreator.setChannelMode(ChannelMode.basic);
        }
        if (isRoleNeeded != null) {
            tempRoomCreator.setRoleNeeded(false);
        }
        if (roleNotFoundMessage != null) {
            tempRoomCreator.setRoleNotFoundMessage(null);
        }
        if (neededRolesIds != null) {
            creatorService.setNeededRolesIds(roomCreator.getIdLong(), Collections.emptyList());
        }
        if (infoChannel != null) {
            tempRoomCreator.setLogChannelId(null);
        }
        creatorService.saveOrUpdate(tempRoomCreator);
    }

    @Subcommand(parentNames = "setup rooms", description = "Сбросить текущие дополнительные настройки для канала-создателя")
    public void reset(SlashCommandInteractionEvent event,
            @Option(name = "creator", description = "Канал-создатель", channelType = ChannelType.VOICE) VoiceChannel roomCreator,
            @Option(name = "category", description = "Категория для нового канала", channelType = ChannelType.CATEGORY) Category category,

            @Option(name = "all", description = "Сбросить все настройки", required = false, choices = {
                    @Choice(name = "Да", value = "yes") }) String all,
            @Option(name = "limit", description = "Сбросить лимит пользователей в новом канале", required = false, choices = {
                    @Choice(name = "Да", value = "yes") }) String userLimit,
            @Option(name = "name", description = "Сбросить начальное название нового канала", required = false, choices = {
                    @Choice(name = "Да", value = "yes") }) String defalutChannelName,
            @Option(name = "mode", description = "Сбросить тип канала", required = false, choices = {
                    @Choice(name = "Да", value = "yes") }) String channelMode,
            @Option(name = "role-needed", description = "Сбросить нужна ли роль для канала", required = false, choices = {
                    @Choice(name = "Да", value = "yes") }) String isRoleNeeded,
            @Option(name = "empty-role-message", description = "Сбросить сообщение, которое отправит бот, если у участника нет нужной роли", required = false, choices = {
                    @Choice(name = "Да", value = "yes") }) String roleNotFoundMessage,
            @Option(name = "roles-ids", description = "Сбросить ID ролей для доступа к каналу. Перечислить сразу все через пробел", required = false, choices = {
                    @Choice(name = "Да", value = "yes") }) String neededRolesIds,
            @Option(name = "info-channel", description = "Сбросить канал, в который будет отправляться информация о созданных ОБЫЧНЫХ каналах", required = false, choices = {
                    @Choice(name = "Да", value = "yes") }) String infoChannel) {
        event.deferReply(true).queue();
        TempRoomCreator tempRoomCreator = creatorService.get(roomCreator.getIdLong());
        if (tempRoomCreator == null) {
            EmbedUtil.replyEmbed(event.getHook(), "Указанный канал и категория не найдены в базе данных", Color.RED);
            return;
        }
        resetSettings(tempRoomCreator, roomCreator, all, userLimit, defalutChannelName, channelMode, isRoleNeeded,
                roleNotFoundMessage, neededRolesIds, infoChannel);
        EmbedUtil.replyEmbed(event.getHook(), "Настройки успешно сброшены", Color.GREEN);
    }

    @Subcommand(parentNames = "setup rooms", description = "Отвязать комнату от бота. Удаляет все настройки заданного канала")
    public void remove(SlashCommandInteractionEvent event,
            @Option(name = "creator", description = "Канал-создатель", channelType = ChannelType.VOICE) VoiceChannel roomCreator) {
        event.deferReply(true).queue();
        TempRoomCreator tempRoomCreator = creatorService.get(roomCreator.getIdLong());
        if (tempRoomCreator == null) {
            EmbedUtil.replyEmbed(event.getHook(), "Указанный канал не найден в базе данных", Color.RED);
            return;
        }

        creatorService.remove(tempRoomCreator);
        EmbedUtil.replyEmbed(event.getHook(), "Настройки удалены", Color.GREEN);
    }
}
