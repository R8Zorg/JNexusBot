package io.nexusbot.modules.globalEvents;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.r8zorg.jdatools.OwnersRegistry;
import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.services.BotOwnerService;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnStartup extends ListenerAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(OnStartup.class);
    private BotOwnerService botOwnerService = new BotOwnerService();

    @Override
    public void onReady(ReadyEvent event) {
        List<Long> botOwnerIds = botOwnerService.getAllIds();
        if (botOwnerIds.isEmpty()) {
            LOGGER.info("Owners list is empty. Adding default value");
            long botOwnerId = 389787190986670082L;
            botOwnerService.add(botOwnerId);
            OwnersRegistry.addOwner(botOwnerId);
        } else {
            OwnersRegistry.setOwners(botOwnerIds);
        }
        LOGGER.info("Owners loaded successfully");
    }
}
