package com.yk;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * LambdaTest
 */

public class LambdaTest
{
    @Test
    public void test()
    {
        List<String> pathList = new ArrayList<>();
        pathList.add("\\aaa\\bbb\\ccc\\");
        pathList.add("\\aaa\\bbb\\eee\\");
        pathList.add("\\aaa\\bbb\\fff\\");
        pathList.add("\\aaa\\bbb\\ddd/");
        pathList.add("\\aaa\\bbb\\hhh/");
        pathList.add("\\aaa\\bbb\\hhh\\");
        
        String a = null;
        Optional.ofNullable(a).ifPresent(System.out::println);
        
        Map<String, String> mapping = Optional.<List<String>>ofNullable(pathList).orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(k -> k.replace("\\", File.separator).replace("/", File.separator), v -> v, (k1, k2) -> k1));
        System.out.println(mapping);
        pathList = mapping.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        System.out.println(pathList);
    
        pathList = new ArrayList<>();
        pathList.add("\\aaa\\bbb\\ccc\\");
        pathList.add("\\aaa\\bbb\\eee\\");
        pathList.add("\\aaa\\bbb\\fff\\");
        pathList.add("\\aaa\\bbb\\ddd/");
        pathList.add("\\aaa\\bbb\\hhh/");
        pathList.add("\\aaa\\bbb\\hhh\\");
        Map<String, List<String>> mapping2 = pathList.stream().collect(Collectors.groupingBy(k -> k));
        System.out.println(mapping2);
        List<String> flatList = mapping2.entrySet().stream().map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        System.out.println(flatList);
    }
}
