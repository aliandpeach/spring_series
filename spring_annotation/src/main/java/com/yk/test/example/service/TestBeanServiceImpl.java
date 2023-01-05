package com.yk.test.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TestBeanServiceImpl {

    @Autowired
    private Optional<TestInnerBeanServiceImpl> testInnerBeanService;

    @Autowired
    @Qualifier("testInnerBeanService")
    private TestInnerBeanServiceImpl service;

    public void test() {
        System.out.print("mapper/test");
//        testInnerBeanService.ifPresent(TestInnerBeanServiceImpl::test);
        List<Map<String, Object>> r
                = Optional.<TestInnerBeanServiceImpl>ofNullable(service).map(t -> t.test(1, "")).get();

        r = testInnerBeanService.map(t -> t.test(1, "")).orElseGet(() -> new ArrayList<>());
        r = testInnerBeanService.map(t -> t.test(1, "")).orElseGet(() -> null);
        r = testInnerBeanService.map(t -> t.test(1, "")).orElse(new ArrayList<>());
    }

    @PostConstruct
    public void pc() {
        System.out.print("@PostConstruct...");
    }
}
