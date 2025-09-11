package io.nexusbot.componentsData;

import java.util.EnumSet;

import net.dv8tion.jda.api.Permission;

public class DiscordConstants {
    public static final int MAX_COMPONENTS = 5;
    public static final int MAX_SELECT_MENU_ITEMS = 25;
    public static final EnumSet<Permission> deniedPermissions = EnumSet.of(
            Permission.MANAGE_CHANNEL, Permission.MANAGE_WEBHOOKS,
            Permission.PRIORITY_SPEAKER, Permission.VOICE_MUTE_OTHERS,
            Permission.VOICE_DEAF_OTHERS, Permission.MESSAGE_MENTION_EVERYONE,
            Permission.MESSAGE_TTS, Permission.CREATE_SCHEDULED_EVENTS,
            Permission.MANAGE_EVENTS);
}
