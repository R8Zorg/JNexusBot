package io.nexusbot.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static HashMap<Long, ChannelOverrides> serrializeOverrides(List<PermissionOverride> overrides) {
        HashMap<Long, ChannelOverrides> serrializedOverrides = new HashMap<>();
        overrides.forEach(po -> {
            long id = po.getIdLong();
            String type = po.isMemberOverride() ? "member" : "role";
            EnumSet<Permission> allow = po.getAllowed();
            EnumSet<Permission> deny = po.getDenied();

            serrializedOverrides.put(id, new ChannelOverrides(type, allow, deny));
        });
        return serrializedOverrides;
    }

    private static void upsertOverrides(VoiceChannel voiceChannel, HashMap<Long, ChannelOverrides> overwrites,
            HashMap<Long, ChannelOverrides> initialOverrides, Long everyoneRoleId) {
        Set<Long> initialOverrideIds = initialOverrides.keySet().stream().collect(Collectors.toSet());

        for (Map.Entry<Long, ChannelOverrides> entry : overwrites.entrySet()) {
            ChannelOverrides channelOverrides = entry.getValue();

            long id = entry.getKey();
            String type = channelOverrides.getType();
            EnumSet<Permission> allow = channelOverrides.getAllow();
            EnumSet<Permission> deny = channelOverrides.getDeny();

            if (allow.isEmpty() && deny.isEmpty()) {
                continue;
            }
            if (id != everyoneRoleId && initialOverrideIds.contains(id)) {
                continue;
            }

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

    public static void updateChannelOverrides(VoiceChannel voiceChannel, HashMap<Long, ChannelOverrides> overwrites,
            HashMap<Long, ChannelOverrides> initialOverrides) {
        upsertOverrides(voiceChannel, overwrites, initialOverrides, null);
    }

    public static void updateChannelOverrides(VoiceChannel voiceChannel, HashMap<Long, ChannelOverrides> overwrites,
            HashMap<Long, ChannelOverrides> initialOverrides, long everyoneRoleId) {
        upsertOverrides(voiceChannel, overwrites, initialOverrides, everyoneRoleId);
    }
}
