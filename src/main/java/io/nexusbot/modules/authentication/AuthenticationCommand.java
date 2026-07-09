package io.nexusbot.modules.authentication;

import java.awt.Color;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import io.github.r8zorg.jdatools.annotations.Command;
import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

@SlashCommands
public class AuthenticationCommand {
    private static final int ANSWERS_COUNT = 4;
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 11;
    private ThreadLocalRandom localRandom = ThreadLocalRandom.current();
    private SpecialRolesService rolesService = new SpecialRolesService();

    private HashSet<Integer> generateAnswers(int num1, int num2, int rightAnswer) {
        var answers = new HashSet<Integer>(ANSWERS_COUNT);
        answers.add(rightAnswer);
        while (answers.size() < ANSWERS_COUNT) {
            answers.add(rightAnswer + localRandom.nextInt(-4, 6));
        }
        return answers;
    }

    private boolean isDefaultRoleExists(Guild guild) {
        Role defaultRole = Optional.ofNullable(rolesService.get(guild.getIdLong()))
                .map(SpecialRoles::getDefaultRoleId)
                .map(guild::getRoleById)
                .orElse(null);
        return defaultRole == null
                ? false
                : true;

    }

    @Command(description = "Создать сообщение для аутентификации участников")
    public void authentication(SlashCommandInteractionEvent event,
            @Option(name = "text", description = "Текст для эмбед сообщения", required = false) String text) {
        text = Objects.requireNonNullElse(text, "Выберите правильный вариант ответа\n");
        if (!isDefaultRoleExists(event.getGuild())) {
            EmbedUtil.replyEmbed(event,
                    "Не найдена стандартная роль. Для использования этой команды укажите её через `/setup role default`",
                    Color.RED);
            return;
        }

        int num1 = localRandom.nextInt(MIN_NUMBER, MAX_NUMBER);
        int num2 = localRandom.nextInt(MIN_NUMBER, MAX_NUMBER);
        int rightAnswer = num1 + num2;

        HashSet<Integer> answers = generateAnswers(num1, num2, rightAnswer);

        var embed = EmbedUtil.generateEmbed(text, Color.CYAN);
        var menuBuilder = StringSelectMenu.create(GlobalIds.AUTHENTICATION_ID.getValue());
        menuBuilder.setPlaceholder(num1 + " + " + num2 + " = ?");
        for (int answer : answers) {
            menuBuilder.addOption(Integer.toString(answer), num1 + " " + num2 + " " + answer);
        }

        event.replyEmbeds(embed).addActionRow(menuBuilder.build()).queue();
    }
}
