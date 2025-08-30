package io.nexusbot.database.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.TempRoomSettings;
import io.nexusbot.database.entities.TempRoomSettingsPK;
import io.nexusbot.database.interfaces.ITempRoomSettings;

public class TempRoomSettingsDao implements ITempRoomSettings {
    @Override
    public TempRoomSettings get(long ownerId, long guildId) {
        TempRoomSettingsPK pk = new TempRoomSettingsPK(ownerId, guildId);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TempRoomSettings.class, pk);
        }
    }

    @Override
    public void saveOrUpdate(TempRoomSettings roomSettings) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction ta = session.beginTransaction();
            session.merge(roomSettings);
            ta.commit();
        }
    }
}
