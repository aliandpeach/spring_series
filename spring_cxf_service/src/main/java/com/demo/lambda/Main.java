package com.demo.lambda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String args[]) {

        /**
         * 类名::实例方法
         */

        Predicate<List<UserModel>> p1 = List::isEmpty;
        p1.test(new ArrayList<>());// 检测new ArrayList是否为空

        BiFunction<String, String, String> f1 = String::concat;
        String r = f1.apply("a", "b"); // r = ab;

        ThreeFunction<UserModel, Integer, String, Map<Integer, String>> f2 = UserModel::increment;
        Map<Integer, String> m1 = f2.apply(new UserModel(), 1, "v1");

        Function<UserModel, Double> f4 = UserModel::getWeight;

        /**
         * 类名::静态方法
         */
        BiFunction<Integer, String, Map<Integer, String>> f3 = UserModel::more;

        /**
         * 实例::方法
         */
        UserModel userModel = new UserModel(12, "Abc", 22, "black");
        BiFunction<Integer, String, Map<Integer, String>> f5 = userModel::increment;

        /**
         * 类名::new
         */

        Supplier<UserModel> s1 = UserModel::new;

        ForthFunction<Integer, String, Double, String, UserModel> s2 = UserModel::new;


        /**
         * Compartor
         */

        List<UserModel> list = new ArrayList<>();
        list.add(new UserModel(12, "John", 20.3, "black"));
        list.add(new UserModel(13, "Carl", 22.5, "yellow"));
        list.add(new UserModel(14, "Divid", 26.1, "red"));
        list.add(new UserModel(11, "Li", 23.0, "black"));
        list.add(new UserModel(13, "Zao", 29.5, "pink"));

        list.sort((o1, o2) -> {
                    int to = Integer.valueOf(o1.getAge()).compareTo(Integer.valueOf(o2.getAge()));
                    if (to != 0) {
                        return to;
                    }
                    to = o1.getName().compareTo(o2.getName());
                    if (to != 0) {
                        return to;
                    }
                    to = Double.valueOf(o1.getWeight()).compareTo(Double.valueOf(o2.getWeight()));
                    if (to != 0) {
                        return to;
                    }
                    to = o1.getHairColor().compareTo(o2.getHairColor());
                    if (to != 0) {
                        return to;
                    }
                    return 0;
                }
        );

        String str = Optional.<List<UserModel>>ofNullable(list).map(l -> l.toString()).get();
        // [UserModel{age=11, name='Li', weight=23.0, hairColor='black'}, UserModel{age=12, name='John', weight=20.3, hairColor='black'}, UserModel{age=13, name='Carl', weight=22.5, hairColor='yellow'}, UserModel{age=14, name='Divid', weight=26.1, hairColor='red'}, UserModel{age=15, name='Zao', weight=29.5, hairColor='pink'}]

        Function<UserModel, Integer> f6 = p -> p.getAge();
        Function<UserModel, String> f7 = p -> p.getName();
        Function<UserModel, Double> f8 = p -> p.getWeight();
        Function<UserModel, String> f9 = p -> p.getHairColor();
        list.sort(Comparator.comparing(f6).thenComparing(f7).thenComparing(f8).thenComparing(f9).reversed());

        Optional.<List<UserModel>>ofNullable(list).map(l -> l.toString()).ifPresent(System.out::println);

        Optional<List<UserModel>> optional = Optional.<List<UserModel>>ofNullable(list).flatMap(l -> Optional.<List<UserModel>>ofNullable(l));

        /**
         * Stream
         */
        // 转换List<UserModel>  为 List<String>
        List<String> stringList1
                = Optional.<List<UserModel>>ofNullable(list).map(l -> l.stream().map(t -> t.toString()).collect(Collectors.toList())).get();

        // 转换List<UserModel>  为 List<String>
        stringList1 = list.stream().map(t -> t.toString()).collect(Collectors.toCollection(() -> new ArrayList<>()));

        // 转换List<UserModel>  为 HashMap<String, UserModel>
        Map<Long, UserModel> m
                = Optional.<List<UserModel>>ofNullable(list).map(l -> l.stream().collect(Collectors.toMap(t -> t.getId(), t -> t, (k1, k2) -> k1))).get();

        // 转换List<UserModel>  为 LinkedHashMap<String, UserModel>
        m = Optional.<List<UserModel>>ofNullable(list).map(l -> l.stream().collect(Collectors.toMap(t -> t.getId(), t -> t, (k1, k2) -> k1, LinkedHashMap::new))).get();

        List<UserModel> lll = Optional.<List<UserModel>>ofNullable(list).map(l -> l.stream().map(t -> t).collect(Collectors.toList())).get();

//        Map<Long, UserModel> mx
//                = Optional.<List<UserModel>>ofNullable(list).map(l -> l.stream().map(t -> t).collect(Collectors.toMap(t->t.getId(), t -> t, (k1, k2) -> k1))).get();

        // 转换List<UserModel>  为 Map<Long, List<UserModel>>
        Map<Long, List<UserModel>> m2 = list.stream().collect(Collectors.groupingBy(t -> t.getId()));

        // 原集合不变，生成了一个新的排好序的集合 //sorted()需要集合内的实体实现 Comparable接口 否则要报错
        List<String> stringList = new ArrayList<>();
        List<String> an = stringList.stream().sorted().collect(Collectors.toList());

        // 排序
        List<UserModel> an1 = list.stream().sorted(Comparator.comparing(f6).thenComparing(f7).thenComparing(f8).thenComparing(f9).reversed()).collect(Collectors.toList());
        System.out.println(an);

        /**
         * list -> map
         */

        Map<Integer, String> map = new HashMap<Integer, String>();
        AtomicInteger integer = new AtomicInteger(0);
        stringList1.stream().forEach(t -> map.merge(integer.incrementAndGet(), t, (map1_value, map2_value) -> map1_value));
        System.out.println();

        Map<Integer, String> map1 = new HashMap<Integer, String>();
        stringList1.stream().forEach(t -> map1.merge(integer.incrementAndGet(), t, (map1_value, map2_value) -> map1_value));

        // 创造一个map中已经存在的key
        map1.put(1, "repeat");
        Map<Integer, String> map3 = new HashMap<Integer, String>(map);
        map1.forEach((ax, bx) -> map3.merge(ax, bx, (map1_value, map2_value) -> map2_value));

        //
        Map<Long, UserModel> mapk = new HashMap<Long, UserModel>();
        Map<Long, UserModel> mapw = new HashMap<Long, UserModel>();
        Map<Long, UserModel> u = Stream.concat(mapk.entrySet().stream(), mapw.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (value1, value2) -> new UserModel()));
        System.out.println();
    }
}
