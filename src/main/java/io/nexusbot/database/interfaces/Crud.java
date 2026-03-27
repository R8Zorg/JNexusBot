package io.nexusbot.database.interfaces;

import java.util.List;

public interface Crud<T, K> {
    void create(T entity);

    T get(K id);

    List<T> getAll();

    void saveOrUpdate(T entity);

    void delete(T entity);
}
