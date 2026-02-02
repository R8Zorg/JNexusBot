package io.nexusbot.componentsData;

import java.util.EnumSet;
import java.util.List;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;

public class DiscordConstants {
    public static final int MAX_COMPONENTS = 5;
    public static final int MAX_SELECT_MENU_ITEMS = 25;
    public static final EnumSet<Permission> deniedPermissions = EnumSet.of(
            Permission.MANAGE_CHANNEL, Permission.MANAGE_WEBHOOKS,
            Permission.PRIORITY_SPEAKER, Permission.VOICE_MUTE_OTHERS,
            Permission.VOICE_DEAF_OTHERS, Permission.MESSAGE_MENTION_EVERYONE,
            Permission.MESSAGE_TTS, Permission.CREATE_SCHEDULED_EVENTS,
            Permission.MANAGE_EVENTS);
    public static final List<Region> REGIONS = List.of(
            Region.AUTOMATIC, Region.BRAZIL,
            Region.HONG_KONG, Region.INDIA,
            Region.JAPAN, Region.ROTTERDAM,
            Region.SINGAPORE, Region.SOUTH_KOREA,
            Region.SOUTH_AFRICA, Region.SYDNEY,
            Region.US_CENTRAL, Region.US_EAST,
            Region.US_SOUTH, Region.US_WEST);
}
