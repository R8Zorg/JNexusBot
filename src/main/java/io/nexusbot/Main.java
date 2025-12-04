package io.nexusbot;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.r8zorg.jdatools.CommandsManager;
import io.github.r8zorg.jdatools.ListenersManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        Dotenv dotenv = Dotenv.load();

        CommandsManager commandsManager = new CommandsManager("io.nexusbot.modules.commands");
        ListenersManager listenersManager = new ListenersManager("io.nexusbot.modules.listeners", commandsManager);

        JDA jda = JDABuilder.createDefault(dotenv.get("TOKEN"), EnumSet.allOf(GatewayIntent.class))
                .addEventListeners(listenersManager.getAllListeners())
                .setActivity(Activity.watching("битвы игроков"))
                .build()
                .awaitReady();
        jda.updateCommands().addCommands(commandsManager.getSlashCommandData()).queue();

        logger.info("Bot {} started", jda.getSelfUser().getName());
    }
}
