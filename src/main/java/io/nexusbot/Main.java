package io.nexusbot;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.r8zorg.jdatools.CommandsManager;
import io.github.r8zorg.jdatools.ListenersManager;
import io.github.r8zorg.jdatools.SlashCommandsHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        Dotenv dotenv = Dotenv.load();

        CommandsManager commandsManager = new CommandsManager("io.nexusbot.modules.commands");
        ListenersManager listenersManager = new ListenersManager("io.nexusbot.modules.listeners");

        EnumSet<GatewayIntent> gatewayIntents = EnumSet.of(
                GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EXPRESSIONS,
                GatewayIntent.SCHEDULED_EVENTS);

        JDA jda = JDABuilder.createDefault(dotenv.get("TOKEN"), gatewayIntents)
                .addEventListeners(new SlashCommandsHandler(commandsManager))
                .build();
        // jda.updateCommands().queue();
        // jda.updateCommands().addCommands(commandsManager.getSlashCommandData()).queue();
        jda.awaitReady();
        // jda.getGuildById("1251126347502325851").updateCommands().queue();
        jda.getGuildById("1251126347502325851").updateCommands().addCommands(commandsManager.getSlashCommandData()).queue();
        listenersManager.registerListeners(jda);

        logger.info("Bot {} started", jda.getSelfUser().getName());
    }
}
