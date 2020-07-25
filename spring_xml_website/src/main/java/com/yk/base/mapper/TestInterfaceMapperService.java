package com.yk.base.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TestInterfaceMapperService {

    @Autowired
    private MapperTemplate mapperTemplate;

    public void test() {
        TestInterfaceMapperDAO dao = mapperTemplate.getMapper(TestInterfaceMapperDAO.class, "com/yk/base/mapper/testInterface.xml");
        List<Map<String, String>> list = dao.testInterfaceMybatisQuery();
        Optional.of(list).ifPresent(System.out::println);
    }
}
