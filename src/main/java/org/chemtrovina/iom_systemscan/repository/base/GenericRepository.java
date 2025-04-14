package org.chemtrovina.iom_systemscan.repository.base;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T> {
    T findById(Long id);
    List<T> findAll();
    void add(T entity);
    void update(T entity);
    void delete(Long id);
}
