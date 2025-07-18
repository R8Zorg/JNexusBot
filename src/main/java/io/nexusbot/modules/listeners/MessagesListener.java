package io.nexusbot.modules.listeners;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class MessagesListener extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(MessagesListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        logger.info("{} wrote: {}", event.getAuthor().getName(), message);
    }
}
