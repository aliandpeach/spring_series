package com.yk.db.jpa.repository;

import java.io.Serializable;
import java.util.List;

public interface IBaseDao<T>
{

    /**
     * 新增
     *
     * @param entity
     */
    void save(T entity);

    /**
     * 更新
     *
     * @param entity
     */
    void update(T entity);

    /**
     * 根据id删除
     *
     * @param id
     */
    void deleteById(Serializable id);


    /**
     * 通过id查找
     *
     * @param id
     * @return 实体
     */
    T findById(Serializable id);


    /**
     * 查找所有
     *
     * @return List集合
     */
    List<T> findAll();
}
