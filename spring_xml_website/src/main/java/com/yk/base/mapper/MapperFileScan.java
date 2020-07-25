package com.yk.base.mapper;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 该类负责扫描mybatis 的 mapper配置文件(xml)
 */
@Component
public class MapperFileScan {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    public void loadMapperFile(String filePath) {
        URL xml = MapperFileScan.class.getClassLoader().getResource(filePath);
        String path = xml.getPath();
        Configuration configuration = sqlSessionFactory.getConfiguration();

        try {
            new XMLMapperBuilder(xml.openStream(), configuration, path, configuration.getSqlFragments()).parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
