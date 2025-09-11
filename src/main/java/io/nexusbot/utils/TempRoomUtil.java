package io.nexusbot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.nexusbot.componentsData.ChannelOverrides;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.services.TempRoomSettingsService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class TempRoomUtil {
    private static TempRoomSettingsService settingsService = new TempRoomSettingsService();

    private static void saveRoomSettings(VoiceChannel voiceChannel, long ownerId, Consumer<TempRoomSettings> action) {
        TempRoomSettings settings = settingsService.getOrCreate(ownerId, voiceChannel.getGuild().getIdLong());
        action.accept(settings);
        settingsService.saveOrUpdate(settings);
    }

    public static void saveOverrides(VoiceChannel voiceChannel, long ownerId) {
        List<ChannelOverrides> overrides = OverridesUtil.serrializeOverrides(voiceChannel.getPermissionOverrides());
        saveRoomSettings(voiceChannel, ownerId,
                settings -> settings.setOverrides(overrides));
    }

    public static void saveOverrides(long ownerId, Role role, Permission[] permissions) {
        List<ChannelOverrides> overrides = new ArrayList<>();
    }

    public static void saveBitrate(VoiceChannel voiceChannel, long ownerId) {
        saveRoomSettings(voiceChannel, ownerId,
                settings -> settings.setBitrate(voiceChannel.getBitrate()));
    }

    public static void saveNsfw(VoiceChannel voiceChannel, long ownerId) {
        saveRoomSettings(voiceChannel, ownerId,
                settings -> settings.setNsfw(voiceChannel.isNSFW()));
    }

    public static void saveName(VoiceChannel voiceChannel, long ownerId) {
        saveRoomSettings(voiceChannel, ownerId,
                settings -> settings.setName(voiceChannel.getName()));
    }

    public static void saveUserLimit(VoiceChannel voiceChannel, long ownerId) {
        saveRoomSettings(voiceChannel, ownerId,
                settings -> settings.setUserLimit(voiceChannel.getUserLimit()));
    }

    public static void saveVoiceStatus(VoiceChannel voiceChannel, long ownerId) {
        saveRoomSettings(voiceChannel, ownerId,
                settings -> settings.setStatus(voiceChannel.getStatus()));
    }
}
