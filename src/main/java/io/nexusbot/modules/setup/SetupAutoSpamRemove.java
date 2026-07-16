package io.nexusbot.modules.setup;

import java.awt.Color;

import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.nexusbot.database.entities.AutoSpamRemove;
import io.nexusbot.database.services.AutoSpamRemoveService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class SetupAutoSpamRemove {
    private AutoSpamRemoveService autoSpamRemoveService = new AutoSpamRemoveService();

    @Subcommand(parentNames = "setup", description = "Вкл./выкл. автоматическое удаление спама")
    public void auto_spam_remover(SlashCommandInteractionEvent event,
            @Option(name = "value", description = "True - вкл., false - выкл.") Boolean value) {
            AutoSpamRemove autoSpamRemove = autoSpamRemoveService.getOrCreate(event.getGuild().getIdLong(), value);
            autoSpamRemove.setTurnOn(value);
            autoSpamRemoveService.saveOrUpdate(autoSpamRemove);
            EmbedUtil.replyEmbed(event, "Сохранено.", Color.GREEN);
    }

}
