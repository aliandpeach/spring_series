package com.yk.db.jpa.repository;

import com.yk.db.jpa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/05 17:37:11
 */
public interface UserRepository
        extends JpaRepository<User, String>, JpaSpecificationExecutor<User>
{

    /**
     * Find all attachment media type.
     *
     * @return list of media type.
     */
    @Query(value = "SELECT u FROM User u")
    List<User> findAll();

    /**
     * Counts by attachment path.
     *
     * @param path attachment path must not be blank
     * @return count of the given path
     */
    long countByName(@NonNull String path);

    User findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);
}
