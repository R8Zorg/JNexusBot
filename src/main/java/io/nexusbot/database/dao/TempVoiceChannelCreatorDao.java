package io.nexusbot.database.dao;

import java.util.List;
import java.util.NoSuchElementException;

import org.hibernate.Session;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.entities.TempVoiceChannelCreator;
import io.nexusbot.database.enums.ChannelMode;
import io.nexusbot.database.interfaces.ITempVoiceChannelCreator;

public class TempVoiceChannelCreatorDao implements ITempVoiceChannelCreator {
    @Override
    public TempVoiceChannelCreator get(long voiceChannelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TempVoiceChannelCreator.class, voiceChannelId);
        }
    }

    private <T> T getFieldByVoiceChannelId(long voiceChannelId, String fieldName, Class<T> fieldType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT t." + fieldName
                    + " FROM TempVoiceChannelCreator t WHERE t.voiceChannelId = :voiceChannelId";
            return session.createQuery(hql, fieldType)
                    .setParameter("voiceChannelId", voiceChannelId)
                    .uniqueResult();
        }
    }

    @Override
    public long getTempVoiceChannelCategoryId(long voiceChannelId) {
        return getFieldByVoiceChannelId(voiceChannelId, "tempVoiceChannelCategoryId", Long.class);
    }

    @Override
    public int getUserLimit(long voiceChannelId) {
        return getFieldByVoiceChannelId(voiceChannelId, "userLimit", Integer.class);
    }

    @Override
    public String getDefaultTempChannelName(long voiceChannelId) {
        return getFieldByVoiceChannelId(voiceChannelId, "defaultTempChannelName", String.class);
    }

    @Override
    public ChannelMode getChannelMode(long voiceChannelId) {
        return getFieldByVoiceChannelId(voiceChannelId, "channelMode", ChannelMode.class);
    }

    @Override
    public boolean getRoleNeeded(long voiceChannelId) {
        return getFieldByVoiceChannelId(voiceChannelId, "roleNeeded", Boolean.class);
    }

    @Override
    public String getRoleNotFoundMessage(long voiceChannelId) {
        return getFieldByVoiceChannelId(voiceChannelId, "roleNotFoundMessage", String.class);
    }

    @Override
    public List<Long> getNeededRolesIds(long voiceChannelId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TempVoiceChannelCreator channelCreator = session.get(TempVoiceChannelCreator.class, voiceChannelId);
            if (channelCreator == null) {
                throw new NoSuchElementException(
                        "TempVoiceChannelCreator not found for voiceChannelId: " + voiceChannelId);
            }
            return channelCreator.getNeededRolesIds();
        }
    }

    @Override
    public long getLogChannelId(long voiceChannelId) {
        return getFieldByVoiceChannelId(voiceChannelId, "logChannelId", Long.class);
    }
}
