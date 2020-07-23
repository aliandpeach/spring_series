package com.yk.base;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * 尝试重写mybatis， NCE
 * @param <M>
 */
@Repository
public class BaseDAO<M extends Serializable> {
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
}
