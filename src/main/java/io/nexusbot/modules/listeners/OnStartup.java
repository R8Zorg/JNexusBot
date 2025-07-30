package io.nexusbot.modules.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.r8zorg.jdatools.OwnersRegistry;
import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.services.BotOwnerService;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnStartup extends ListenerAdapter {
    final static Logger LOGGER = LoggerFactory.getLogger(OnStartup.class);

    @Override
    public void onReady(ReadyEvent event) {
        BotOwnerService botOwnerService = new BotOwnerService();       
        OwnersRegistry.setOwners(botOwnerService.getAllIds());
        LOGGER.info("Owners loaded successfully");
    }
}
