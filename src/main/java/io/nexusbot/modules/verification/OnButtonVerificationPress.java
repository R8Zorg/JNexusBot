package io.nexusbot.modules.verification;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.GlobalIds;
import io.nexusbot.database.services.VerificationService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

@EventListeners
public class OnButtonVerificationPress extends ListenerAdapter {
    private static final int ANSWERS_COUNT = 4;
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 11;
    private ThreadLocalRandom localRandom = ThreadLocalRandom.current();

    private VerificationService verificationService = new VerificationService();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals(GlobalIds.VERIFICATION_BUTTON.getValue())) {
            return;
        }

        long memberId = event.getMember().getIdLong();
        long guildId = event.getGuild().getIdLong();
        var verification = verificationService.getOrCreate(memberId, guildId);

        OffsetDateTime userTimeout = verification.getMemberTimeout();
        if (userTimeout != null && OffsetDateTime.now().isBefore(userTimeout)) {
            EmbedUtil.replyEmbed(event, "Попытки сбросятся <t:" + userTimeout.toEpochSecond() + ":R>", Color.RED);
            return;
        }
        int num1 = localRandom.nextInt(MIN_NUMBER, MAX_NUMBER);
        int num2 = localRandom.nextInt(MIN_NUMBER, MAX_NUMBER);
        int rightAnswer = num1 + num2;

        HashSet<Integer> answers = generateAnswers(num1, num2, rightAnswer);

        String expression = num1 + " + " + num2 + " = ?";

        var embed = EmbedUtil.generateEmbed("Выберите правильный ответ:\n" + expression, Color.CYAN);
        var menuBuilder = StringSelectMenu.create(GlobalIds.VERIFICATION_BUTTON.getValue());
        menuBuilder.setPlaceholder(expression);
        for (int answer : answers) {
            menuBuilder.addOption(Integer.toString(answer), num1 + " " + num2 + " " + answer);
        }

        event.replyEmbeds(embed).addActionRow(menuBuilder.build()).setEphemeral(true).queue();
    }

    private HashSet<Integer> generateAnswers(int num1, int num2, int rightAnswer) {
        var answers = new HashSet<Integer>(ANSWERS_COUNT);
        answers.add(rightAnswer);
        while (answers.size() < ANSWERS_COUNT) {
            answers.add(rightAnswer + localRandom.nextInt(-4, 6));
        }
        return answers;
    }
}
