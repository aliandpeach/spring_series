package com.yk.db.jpa.repository;

import com.yk.db.jpa.model.Role;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/05 17:37:11
 */
@Repository
@Transactional
public class RoleRepository extends HibernateDaoSupport
{
    public RoleRepository(SessionFactory sessionfactory)
    {
        setSessionFactory(sessionfactory);
    }

    public List<Role> findAll()
    {
        return getHibernateTemplate().loadAll(Role.class);
    }

    public Role findRoleByName(String name)
    {
        return this.getSessionFactory().getCurrentSession().createQuery("select r from Role r where r.name = :name", Role.class)
                .setParameter("name", name).uniqueResult();
    }
}
