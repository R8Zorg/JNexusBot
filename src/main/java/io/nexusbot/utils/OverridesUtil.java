package io.nexusbot.utils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nexusbot.componentsData.ChannelOverrides;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class OverridesUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverridesUtil.class);
    private static final EnumSet<Permission> deniedPermissions = EnumSet.of(
            Permission.MANAGE_CHANNEL, Permission.MANAGE_WEBHOOKS,
            Permission.PRIORITY_SPEAKER, Permission.VOICE_MUTE_OTHERS,
            Permission.VOICE_DEAF_OTHERS, Permission.MESSAGE_MENTION_EVERYONE,
            Permission.MESSAGE_TTS, Permission.CREATE_SCHEDULED_EVENTS,
            Permission.MANAGE_EVENTS);

    public static List<ChannelOverrides> serrializeOverrides(List<PermissionOverride> overrides) {
        List<ChannelOverrides> overwrites = new ArrayList<>();
        overrides.forEach(po -> {
            String id = po.getId();
            String type = po.isMemberOverride() ? "member" : "role";
            EnumSet<Permission> allow = po.getAllowed();
            EnumSet<Permission> deny = po.getDenied();

            overwrites.add(new ChannelOverrides(id, type, allow, deny));
        });
        return overwrites;
    }

    private static void upsertOverrides(VoiceChannel voiceChannel, List<ChannelOverrides> overwrites,
            List<ChannelOverrides> initialOverrides, String everyoneRoleId) {
        Set<String> initialOverridesIds = initialOverrides.stream()
                .map(ChannelOverrides::getId)
                .collect(Collectors.toSet());

        for (ChannelOverrides permissionOverwrite : overwrites) {
            String id = permissionOverwrite.getId();
            String type = permissionOverwrite.getType();
            EnumSet<Permission> allow = permissionOverwrite.getAllow();
            EnumSet<Permission> deny = permissionOverwrite.getDeny();

            if (allow.isEmpty() && deny.isEmpty()) {
                continue;
            }
            if (!id.equals(everyoneRoleId) && initialOverridesIds.contains(id)) {
                continue;
            }
            allow.removeAll(deniedPermissions);
            deny.removeAll(deniedPermissions);

            if (type.equals("role")) {
                Role role = voiceChannel.getGuild().getRoleById(id);
                if (role != null) {
                    voiceChannel.upsertPermissionOverride(role)
                            .grant(allow)
                            .deny(deny)
                            .queue(_ -> {
                            }, error -> LOGGER.info("Error while upserting role permissions: {}", error.getMessage()));
                }
            } else if (type.equals("member")) {
                voiceChannel.getGuild().retrieveMemberById(id).queue(member -> {
                    if (member != null) {
                        voiceChannel.upsertPermissionOverride(member)
                                .grant(allow)
                                .deny(deny)
                                .queue(_ -> {
                                }, error -> LOGGER.info("Error while upserting member permissions: {}",
                                        error.getMessage()));
                    }
                });
            }
        }
    }

    public static void updateChannelOverrides(VoiceChannel voiceChannel, List<ChannelOverrides> overwrites,
            List<ChannelOverrides> initialOverrides) {
        upsertOverrides(voiceChannel, overwrites, initialOverrides, "");
    }

    public static void updateChannelOverrides(VoiceChannel voiceChannel, List<ChannelOverrides> overwrites,
            List<ChannelOverrides> initialOverrides, String everyoneRoleId) {
        upsertOverrides(voiceChannel, overwrites, initialOverrides, everyoneRoleId);
    }
}
