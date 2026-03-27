package io.nexusbot.database.dao;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.Transaction;

import io.nexusbot.database.HibernateUtil;
import io.nexusbot.database.interfaces.Crud;

public abstract class AbstractCrudDao<T, K> implements Crud<T, K> {
    private final Class<T> entityClass;

    public AbstractCrudDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    private <R> R executeInTransaction(Function<Session, R> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                R result = action.apply(session);
                tx.commit();
                return result;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    private void executeInVoidTransaction(Consumer<Session> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                action.accept(session);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    @Override
    public void create(Object entity) {
        executeInVoidTransaction(session -> session.persist(entity));
    }

    @Override
    public T get(K id) {
        return executeInTransaction(session -> session.get(entityClass, id));
    }

    @Override
    public List<T> getAll() {
        return executeInTransaction(
                session -> session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list());
    }

    @Override
    public void saveOrUpdate(Object entity) {
        executeInVoidTransaction(session -> session.merge(entity));
    }

    @Override
    public void delete(Object entity) {
        executeInVoidTransaction(session -> session.remove(entity));
    }

}
