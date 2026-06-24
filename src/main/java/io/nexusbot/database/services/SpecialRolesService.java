package io.nexusbot.database.services;

import io.nexusbot.database.dao.CrudDao;
import io.nexusbot.database.entities.SpecialRoles;

public class SpecialRolesService extends CrudDao<SpecialRoles, Long> {
    public SpecialRolesService() {
        super(SpecialRoles.class);
    }

    @Override
    public SpecialRoles get(Long guildId) {
        return super.get(guildId);
    }


    public SpecialRoles getOrCreate(Long guildId) {
        SpecialRoles specialRoles = super.get(guildId);
        if (specialRoles == null) {
            specialRoles = new SpecialRoles(guildId);
        }
        return specialRoles;
    }
}
