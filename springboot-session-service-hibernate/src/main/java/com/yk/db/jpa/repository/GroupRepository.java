package com.yk.db.jpa.repository;

import com.yk.db.jpa.model.Group;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/05 17:37:11
 */
@Repository
// @Transactional // 注释掉, 使用切面配置事务
public class GroupRepository extends HibernateDaoSupport
{
    public GroupRepository(SessionFactory sessionFactory, HibernateTemplate hibernateTemplate)
    {
        setHibernateTemplate(hibernateTemplate);
        setSessionFactory(sessionFactory);
    }

    public List<Group> findAll()
    {
        return getHibernateTemplate().loadAll(Group.class);
    }

    public Group findByName(String name)
    {
        Group group = new Group();
        group.setName(name);
        // 1.
        List<Group> groups1 = (List<Group>) getHibernateTemplate().findByCriteria(
                DetachedCriteria.forClass(Group.class)
                        .add(Restrictions.eq("name", name)));

        // 2.
        List<Group> groups2 = (List<Group>) getHibernateTemplate().findByCriteria(
                DetachedCriteria.forClass(Group.class)
                        .add(Example.create(group)));
        Session session = getSessionFactory().getCurrentSession();

        // 3.
        Criteria c = session.createCriteria(Group.class);
        c.add(Restrictions.eq("name", name));
        List<Group> groups3 = c.list();

        // 4.
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Group> criteriaQuery = builder.createQuery(Group.class);
        Root<Group> root = criteriaQuery.from(Group.class);
        // 封装查询条件
        Predicate predicate = builder.equal(root.get("name"), name);
        criteriaQuery.where(predicate);
        // 执行查询
        TypedQuery<Group> typeQuery = session.createQuery(criteriaQuery);
        List<Group> groups4 = typeQuery.getResultList();

        return getSessionFactory().getCurrentSession().createQuery("select g from Group g where g.name = :name", Group.class)
                .setParameter("name", name).uniqueResult();
    }
}
