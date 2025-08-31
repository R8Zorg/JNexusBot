package io.nexusbot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nexusbot.componentsData.ChannelOverrides;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class OverridesUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverridesUtil.class);

    public static List<ChannelOverrides> serrializeOverrides(List<PermissionOverride> overrides) {
        List<ChannelOverrides> overwrites = new ArrayList<>();
        overrides.forEach(po -> {
            String id = po.getId();
            String type = po.isMemberOverride() ? "member" : "role";
            long allow = po.getAllowedRaw();
            long deny = po.getDeniedRaw();

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
            long allow = permissionOverwrite.getAllow();
            long deny = permissionOverwrite.getDeny();

            if (!id.equals(everyoneRoleId) && initialOverridesIds.contains(id)) {
                continue;
            }

            if (type.equals("role")) {
                Role role = voiceChannel.getGuild().getRoleById(id);
                if (role != null) {
                    voiceChannel.upsertPermissionOverride(role)
                            .setAllowed(allow)
                            .setDenied(deny)
                            .queue(_ -> {
                            }, error -> LOGGER.info("Error while upserting role permissions: {}", error.getMessage()));
                }
            } else if (type.equals("member")) {
                voiceChannel.getGuild().retrieveMemberById(id).queue(member -> {
                    if (member != null) {
                        voiceChannel.upsertPermissionOverride(member)
                            .setAllowed(allow)
                            .setDenied(deny)
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

    public static void updateChannelOverrides(TextChannel textChannel, List<ChannelOverrides> overwrites) {
        if (overwrites == null || overwrites.isEmpty()) {
            return;
        }
        for (ChannelOverrides permissionOverwrite : overwrites) {
            String id = permissionOverwrite.getId();
            String type = permissionOverwrite.getType();
            long allow = permissionOverwrite.getAllow();
            long deny = permissionOverwrite.getDeny();

            if (type.equals("role")) {
                Role role = textChannel.getGuild().getRoleById(id);
                if (role != null) {
                    textChannel.upsertPermissionOverride(role)
                            .setAllowed(allow)
                            .setDenied(deny)
                            .queue();
                }
            } else if (type.equals("member")) {
                Member member = textChannel.getGuild().getMemberById(id);
                if (member != null) {
                    textChannel.upsertPermissionOverride(member)
                            .setAllowed(allow)
                            .setDenied(deny)
                            .queue();
                }
            }
        }
    }
}
