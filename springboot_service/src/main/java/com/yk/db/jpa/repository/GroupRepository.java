package com.yk.db.jpa.repository;

import com.yk.db.jpa.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/05 17:37:11
 */
public interface GroupRepository
        extends JpaRepository<Group, String>, JpaSpecificationExecutor<Group>
{

    /**
     * Find all attachment media type.
     *
     * @return list of media type.
     */
    @Query(value = "SELECT g FROM Group g")
    List<Group> findAll();

    /**
     * Counts by attachment path.
     *
     * @param path attachment path must not be blank
     * @return count of the given path
     */
    long countByName(@NonNull String path);

    Group findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);
}
