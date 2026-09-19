package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Репозиторий для управления сущностью {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Проверяет, существует ли пользователь с указанным идентификатором.
     *
     * @param id идентификатор пользователя
     * @return {@code true}, если пользователь существует, иначе {@code false}
     */
    boolean existsUserById(Long id);
}
