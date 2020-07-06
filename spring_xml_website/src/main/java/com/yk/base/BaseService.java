package com.yk.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public abstract class BaseService<M extends Serializable> {
    @Autowired
    protected BaseDAO<M> baseDAO;
}
