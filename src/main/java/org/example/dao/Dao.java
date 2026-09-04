package org.example.dao;

import java.util.List;
import java.util.Optional;

public interface Dao <T>{
    T save(T object);
    Optional<T> findById(Long id);
    List<T> findAll();
    T update(T object);
    void delete(Long id);
}
