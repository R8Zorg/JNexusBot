package io.nexusbot.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import io.nexusbot.database.entities.Blacklist;
import io.nexusbot.database.services.BlacklistService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;

public class MembersUtil {
    private static BlacklistService blacklistService = new BlacklistService();

    public static CompletableFuture<List<Member>> loadMembers(GenericSelectMenuInteractionEvent<?, ?> event,
            List<Long> memberIds) {
        CompletableFuture<List<Member>> future = new CompletableFuture<>();

        AtomicInteger counter = new AtomicInteger(memberIds.size());
        List<Member> members = Collections.synchronizedList(new ArrayList<>());

        for (Long memberId : memberIds) {
            event.getGuild().retrieveMemberById(memberId).queue(
                    member -> {
                        members.add(member);
                        if (counter.decrementAndGet() == 0) {
                            future.complete(members);
                        }
                    }, error -> {
                        if (counter.decrementAndGet() == 0) {
                            future.complete(members);
                        }
                    });
        }
        return future;
    }

    public static boolean inBlacklist(long userId) {
        Blacklist blacklist = blacklistService.get(userId);
        if (blacklist == null) {
            return false;
        }
        return true;

    }
}
