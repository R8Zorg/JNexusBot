package io.nexusbot.modules.listeners.tempRooms.selectMenu;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.componentsData.DiscordConstants;
import io.nexusbot.componentsData.TempRoomSettingsMenu;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class OnRoomSettingsSubMenuSelect extends ListenerAdapter {
    private final Map<String, Consumer<StringSelectInteractionEvent>> stringMenuHandler = new HashMap<>();

    public OnRoomSettingsSubMenuSelect() {
        stringMenuHandler.put(TempRoomSettingsMenu.REGION.getValue(), this::changeRegion);
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        Consumer<StringSelectInteractionEvent> handler = stringMenuHandler.get(event.getComponentId());
        if (handler != null) {
            handler.accept(event);
        }
    }

    private void changeRegion(StringSelectInteractionEvent event) {
        // event.deferEdit().queue();
        String selectedRegion = event.getSelectedOptions()
                .get(0).getValue();
        VoiceChannel voiceChannel = event.getChannel().asVoiceChannel();
        Region region = DiscordConstants.REGIONS
                .stream()
                .filter(_region -> _region.getKey().equals(selectedRegion))
                .findFirst()
                .orElseThrow();
        voiceChannel.getManager().setRegion(region).queue(
                success -> EmbedUtil.replyEmbed(event, "Регион изменён на: " + region.getName(), Color.GREEN),
                error -> EmbedUtil.replyEmbed(event, "Не удалось сменить регион: " + error.getMessage(), Color.RED));
    }
}
