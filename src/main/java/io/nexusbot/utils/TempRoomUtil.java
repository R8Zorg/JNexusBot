package io.nexusbot.utils;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import io.nexusbot.componentsData.ChannelOverrides;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.services.TempRoomSettingsService;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class TempRoomUtil {
    private static TempRoomSettingsService settingsService = new TempRoomSettingsService();

    private static void saveRoomSettings(VoiceChannel voiceChannel, long ownerId, Consumer<TempRoomSettings> action) {
        TempRoomSettings settings = settingsService.getOrCreate(ownerId, voiceChannel.getGuild().getIdLong());
        action.accept(settings);
        settingsService.saveOrUpdate(settings);
    }

    private static void setOverrides(TempRoomSettings settings, PermissionOverride override) {
        ChannelOverrides newOverrides = new ChannelOverrides(override.isRoleOverride() ? "role" : "member",
                override.getAllowed(), override.getDenied());
        HashMap<Long, ChannelOverrides> existingOverrides = settings.getOverrides();
        if (override.getAllowed().isEmpty() && override.getDenied().isEmpty()) {
            existingOverrides.remove(override.getIdLong());
        } else {
            existingOverrides.put(override.getIdLong(), newOverrides);
        }

        settings.setOverrides(existingOverrides);
    }

    public static void saveOverrides(long ownerId, PermissionOverride override) {
        TempRoomSettings settings = settingsService.getOrCreate(ownerId, override.getGuild().getIdLong());
        setOverrides(settings, override);
        settingsService.saveOrUpdate(settings);
    }

    public static void saveOverrides(long ownerId, List<PermissionOverride> overrides) {
        TempRoomSettings settings = settingsService.getOrCreate(ownerId, overrides.get(0).getGuild().getIdLong());

        for (PermissionOverride permissionOverride : overrides) {
            setOverrides(settings, permissionOverride);
        }
        settingsService.saveOrUpdate(settings);
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
