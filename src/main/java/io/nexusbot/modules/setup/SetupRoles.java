package io.nexusbot.modules.setup;

import java.awt.Color;

import io.github.r8zorg.jdatools.annotations.Option;
import io.github.r8zorg.jdatools.annotations.SlashCommands;
import io.github.r8zorg.jdatools.annotations.Subcommand;
import io.github.r8zorg.jdatools.annotations.SubcommandGroup;
import io.nexusbot.database.entities.SpecialRoles;
import io.nexusbot.database.services.SpecialRolesService;
import io.nexusbot.utils.EmbedUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SlashCommands
public class SetupRoles {
    private SpecialRolesService specialRolesService = new SpecialRolesService();

    @SubcommandGroup(parentName = "setup", description = "Группа команд для указания ролей")
    public void role(SlashCommandInteractionEvent event) {
    }

    @Subcommand(parentNames = "setup role")
    public void mute(SlashCommandInteractionEvent event,
            @Option(name = "role", description = "Мьют роль") Role role) {
            SpecialRoles specialRoles = specialRolesService.getOrCreate(event.getGuild().getIdLong());
            specialRoles.setMuteRoleId(role.getIdLong());
            specialRolesService.saveOrUpdate(specialRoles);
            EmbedUtil.replyEmbed(event, "Мьют-роль сохранена.", Color.GREEN);
    }

}
