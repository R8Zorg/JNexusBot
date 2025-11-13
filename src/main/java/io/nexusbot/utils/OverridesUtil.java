package io.nexusbot.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nexusbot.componentsData.ChannelOverrides;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public class OverridesUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverridesUtil.class);

    public static HashMap<Long, ChannelOverrides> serrializeOverrides(List<PermissionOverride> overrides) {
        HashMap<Long, ChannelOverrides> serrializedOverrides = new HashMap<>();
        overrides.forEach(po -> {
            long id = po.getIdLong();
            String type = po.isMemberOverride() ? "member" : "role";
            long allow = po.getAllowedRaw();
            long deny = po.getDeniedRaw();

            serrializedOverrides.put(id, new ChannelOverrides(type, allow, deny));
        });
        return serrializedOverrides;
    }

    public static List<EnumSet<Permission>> upsertOverrides(
            ChannelAction<VoiceChannel> voiceChannel, EnumSet<Permission> permissions,
            PermissionOverride categoryOverrides) {
        EnumSet<Permission> allowedPermsssions = EnumSet.noneOf(Permission.class);
        EnumSet<Permission> deniedPermsssions = EnumSet.noneOf(Permission.class);
        if (categoryOverrides != null) {
            allowedPermsssions.addAll(categoryOverrides.getAllowed());
            deniedPermsssions.addAll(categoryOverrides.getDenied());
        }
        allowedPermsssions.addAll(permissions);
        return Arrays.asList(allowedPermsssions, deniedPermsssions);
    }

    // public static void updateChannelOverrides(VoiceChannel voiceChannel,
    // HashMap<Long, ChannelOverrides> overwrites,
    // HashMap<Long, ChannelOverrides> initialOverrides) {
    // upsertOverrides(voiceChannel, overwrites, initialOverrides, null);
    // }
    //
    // public static void updateChannelOverrides(VoiceChannel voiceChannel,
    // HashMap<Long, ChannelOverrides> overwrites,
    // HashMap<Long, ChannelOverrides> initialOverrides, long everyoneRoleId) {
    // upsertOverrides(voiceChannel, overwrites, initialOverrides, everyoneRoleId);
    // }
}
