package com.yk.db.jpa.repository;

import com.yk.db.jpa.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/05 17:37:11
 */
public interface RoleRepository
        extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role>
{

    /**
     * Find all attachment media type.
     *
     * @return list of media type.
     */
    @Query(value = "SELECT r FROM Role r")
    List<Role> findAll();

    Role findRoleByName(String name);
}
