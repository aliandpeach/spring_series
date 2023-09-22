package com.yk.db.jpa.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseDaoImpl<T> extends HibernateDaoSupport implements IBaseDao<T>
{

    private Class<T> clazz;

    // 按照类型自动注入SessionFactory; 在实例化的时候,Spring按照形参的类型自动注入
    @Autowired
    public void setMySessionFactory(SessionFactory sessionFactory)
    {
        setSessionFactory(sessionFactory);
    }


    public BaseDaoImpl()
    {
        //this表示当前被实例化的对象；如UserDaoImpl
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();//BaseDaoImpl<User>
        clazz = (Class<T>) pt.getActualTypeArguments()[0];
    }

    /**
     * 获取session
     *
     * @return session
     */
    public Session getCurrentSession()
    {
        Session session = null;
        try
        {
            session = getSessionFactory().getCurrentSession();
        }
        catch (HibernateException e)
        {
            throw new RuntimeException("getCurrentSession error", e);
        }
        return session;
    }

    @Override
    public void save(T entity)
    {
        getHibernateTemplate().save(entity);
    }

    @Override
    public void update(T entity)
    {
        getHibernateTemplate().update(entity);
    }

    @Override
    public void deleteById(Serializable id)
    {
        getHibernateTemplate().delete(findById(id));
    }

    @Override
    public T findById(Serializable id)
    {
        return getHibernateTemplate().get(clazz, id);
    }

    @Override
    public List<T> findAll()
    {
        Session session = getCurrentSession();
        Query query = session.createQuery("FROM " + clazz.getSimpleName());
        return query.list();
    }

}
