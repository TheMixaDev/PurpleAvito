package org.bigbrainmm.avitoadminapi.repository;

import org.bigbrainmm.avitoadminapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Интерфейс работы с базой данных для пользователя
 * Используется для авторизации
 * Но так как сказали, что она не нужна, он как бы есть
 * Но как бы не используется... {@link User}
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find by username optional.
     *
     * @param username the username
     * @return the optional
     */
    Optional<User> findByUsername(String username);

    /**
     * Exists by username boolean.
     *
     * @param username the username
     * @return the boolean
     */
    boolean existsByUsername(String username);
}
