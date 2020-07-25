package com.yk.base.mapper;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperTemplate {

    @Autowired
    private MapperFileScan mapperFileScan;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public <T> T getMapper(Class<T> clazz, String path) {
        mapperFileScan.loadMapperFile(path);
        T t = sqlSessionTemplate.getMapper(clazz);
        return t;
    }
}
