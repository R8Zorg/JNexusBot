package io.nexusbot.modules.listeners.tempRooms;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.r8zorg.jdatools.annotations.EventListeners;
import io.nexusbot.database.entities.TempRoom;
import io.nexusbot.database.services.TempRoomService;
import io.nexusbot.utils.TempRoomUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateBitrateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateUserLimitEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateVoiceStatusEvent;
import net.dv8tion.jda.api.events.channel.update.GenericChannelUpdateEvent;
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@EventListeners
public class onRoomUpdateSettings extends ListenerAdapter {
    private Logger LOGGER = LoggerFactory.getLogger(onRoomUpdateSettings.class);
    private TempRoomService roomService = new TempRoomService();
    private final EnumSet<Permission> deniedPermissions = EnumSet.of(
            Permission.MANAGE_CHANNEL, Permission.MANAGE_WEBHOOKS,
            Permission.PRIORITY_SPEAKER, Permission.VOICE_MUTE_OTHERS,
            Permission.VOICE_DEAF_OTHERS, Permission.MESSAGE_MENTION_EVERYONE,
            Permission.MESSAGE_TTS, Permission.CREATE_SCHEDULED_EVENTS,
            Permission.MANAGE_EVENTS);
    // private final List<Permission> deniedPermissions = List.of(
    // Permission.MANAGE_CHANNEL, Permission.MANAGE_WEBHOOKS,
    // Permission.PRIORITY_SPEAKER, Permission.VOICE_MUTE_OTHERS,
    // Permission.VOICE_DEAF_OTHERS, Permission.MESSAGE_MENTION_EVERYONE,
    // Permission.MESSAGE_TTS, Permission.CREATE_SCHEDULED_EVENTS,
    // Permission.MANAGE_EVENTS);

    private void handleChannelUpdate(GenericChannelUpdateEvent<?> event, BiConsumer<VoiceChannel, Long> action) {
        TempRoom room = roomService.get(event.getChannel().getIdLong());
        if (room != null) {
            action.accept(event.getChannel().asVoiceChannel(), room.getOwnerId());
        }
    }

    private void handleChannelUpdate(GenericPermissionOverrideEvent event, BiConsumer<VoiceChannel, Long> action) {
        TempRoom room = roomService.get(event.getChannel().getIdLong());
        if (room != null) {
            action.accept(event.getChannel().asVoiceChannel(), room.getOwnerId());
        }
    }

    @Override
    public void onChannelUpdateBitrate(ChannelUpdateBitrateEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveBitrate);
    }

    @Override
    public void onChannelUpdateNSFW(ChannelUpdateNSFWEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveNsfw);
    }

    @Override
    public void onChannelUpdateName(ChannelUpdateNameEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveName);
    }

    @Override
    public void onChannelUpdateUserLimit(ChannelUpdateUserLimitEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveUserLimit);
    }

    @Override
    public void onChannelUpdateVoiceStatus(ChannelUpdateVoiceStatusEvent event) {
        handleChannelUpdate(event, TempRoomUtil::saveVoiceStatus);
    }

    // WARN: overrides saves all category settings and loads it in new channel
    // event if channel's category doesn't have this overrides
    @Override
    public void onPermissionOverrideUpdate(PermissionOverrideUpdateEvent event) {
        // LOGGER.info("Override update");
        // TODO: убрать из списка права, которые есть в deniedPermissions
        // TODO: create new PermissionOverride, with safe overrides

        VoiceChannel voiceChannel = event.getChannel().asVoiceChannel();

        List<PermissionOverride> initialPermissionOverrides = voiceChannel.getPermissionOverrides();
        List<PermissionOverride> safePermissionOverrides = new ArrayList<>();
        
        for (PermissionOverride permissionOverride : initialPermissionOverrides) {
            PermissionOverride override = permissionOverride;

            EnumSet<Permission> allowed = permissionOverride.getAllowed();
            allowed.removeAll(deniedPermissions);
            override.getManager().setAllowed(allowed);

            EnumSet<Permission> denied = permissionOverride.getDenied();
            denied.removeAll(deniedPermissions);
            override.getManager().setDenied(denied);

            safePermissionOverrides.add(override);
        }
        LOGGER.info("Initial overrides:");
        for (PermissionOverride permissionOverride : initialPermissionOverrides) {
            LOGGER.info(permissionOverride.getAllowed().toString());
            LOGGER.info(permissionOverride.getDenied().toString());
        }
        LOGGER.info("Initial overrides:");
        for (PermissionOverride permissionOverride : initialPermissionOverrides) {
            LOGGER.info(permissionOverride.getAllowed().toString());
            LOGGER.info(permissionOverride.getDenied().toString());
        }

        // handleChannelUpdate(event, TempRoomUtil::saveOverrides);
    }

    @Override
    public void onPermissionOverrideDelete(PermissionOverrideDeleteEvent event) {
        // LOGGER.info("Override delete");
        // handleChannelUpdate(event, TempRoomUtil::saveOverrides);
    }

}
