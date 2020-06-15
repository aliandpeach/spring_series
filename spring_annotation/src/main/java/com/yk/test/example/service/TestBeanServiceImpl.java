package com.yk.test.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class TestBeanServiceImpl {

    @Autowired
    private Optional<TestInnerBeanServiceImpl> testInnerBeanService;


    public void test() {
        System.out.print("mapper/test");
        testInnerBeanService.ifPresent(TestInnerBeanServiceImpl::test);
    }

    @PostConstruct
    public void pc() {
        System.out.print("@PostConstruct...");
    }
}
