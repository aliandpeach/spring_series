package com.yk.db.jpa.repository;

import com.yk.db.jpa.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.orm.hibernate5.HibernateTemplate;
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
@Transactional // 未配置会产生异常: Could not obtain transaction-synchronized Session for current thread
public class UserRepository extends HibernateDaoSupport
{
    @Autowired
    private SessionFactory sessionFactory;

    public UserRepository(HibernateTemplate hibernateTemplate)
    {
        setHibernateTemplate(hibernateTemplate);
    }

    public void save(User user)
    {
        getHibernateTemplate().save(user);
    }

    public List<User> findAll()
    {
        return getHibernateTemplate().loadAll(User.class);
    }

    public long countByName(@NonNull String name)
    {
        return sessionFactory.getCurrentSession().createQuery("select count(name) from User where name = :name", long.class)
                .setParameter("name", name).uniqueResult();
    }

    public User findByName(String name)
    {
        return sessionFactory.getCurrentSession().createQuery("select r from User r where r.name = :name", User.class)
                .setParameter("name", name).uniqueResult();
    }

    public boolean existsByName(String name)
    {
        return sessionFactory.getCurrentSession().createQuery("select count(name) from User where name = :name", int.class)
                .setParameter("name", name).uniqueResult() > 0;
    }

    public void deleteByName(String name)
    {
        sessionFactory.getCurrentSession().createQuery("delete from User where name = :name")
                .setParameter("name", name);
    }
}
