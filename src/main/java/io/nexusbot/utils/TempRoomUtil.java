package io.nexusbot.utils;

import java.util.List;

import io.nexusbot.componentsData.ChannelOverrides;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.services.TempRoomSettingsService;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class TempRoomUtil {
    private static TempRoomSettingsService settingsService = new TempRoomSettingsService();

    public static void saveOverrides(VoiceChannel voiceChannel, long ownerId) {
        TempRoomSettings settings = settingsService.getOrCreate(ownerId, voiceChannel.getGuild().getIdLong());
        List<ChannelOverrides> overrides = OverridesUtil.serrializeOverrides(voiceChannel.getPermissionOverrides());
        settings.setOverrides(overrides);
        settingsService.saveOrUpdate(settings);
    }
}
