package com.demo.lambda;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

        AtomicLong integer1 = new AtomicLong(0);
        IntStream.range(0, 5).forEach((t) -> mapk.put(integer1.incrementAndGet(), new UserModel(integer1.get(), "AUser_" + integer1.get())));
        integer1.getAndDecrement();
        IntStream.range(0, 5).forEach((t) -> mapw.put(integer1.incrementAndGet(), new UserModel(integer1.get(), "BUser_" + integer1.get())));

        Map<Long, UserModel> u = Stream.concat(mapk.entrySet().stream(), mapw.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (value1, value2) -> new UserModel(value1.getId(), value1.getName())));

        u = Stream.of(mapk.entrySet().stream(), mapw.entrySet().stream())
                .flatMap(t -> t).collect(Collectors.toMap(t -> t.getKey(), t -> t.getValue(), (tz, ty) -> new UserModel(ty.getId(), ty.getName()), () -> new LinkedHashMap<>()));

        u = mapk.entrySet().stream()
                .collect(Collectors.toMap(t -> t.getKey(), t -> t.getValue(), (tz, ty) -> new UserModel(tz.getId(), tz.getName()), () -> new LinkedHashMap<>(mapw)));
        System.out.println();

        Field[] fields = UserModel.class.getDeclaredFields();
        List<Map<String, Field>> llll = Arrays.stream(fields).map(t -> new HashMap<String, Field>(Collections.singletonMap(t.getAnnotation(Value.class).value(), t))).collect(Collectors.toList());
        Map<String, Field> fieldValues2 = llll.stream().flatMap(t -> t.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1));

        Map<String, Field> fieldValues = Arrays.stream(fields).collect(Collectors.toMap(t -> t.getAnnotation(Value.class).value(), t -> t, (k1, k2) -> k1));

        Map<String, List<Field>> temp = Arrays.stream(fields).collect(Collectors.groupingBy(t -> t.getAnnotation(Value.class).value()));
        Map<String, Field> fieldValues3 = temp.entrySet().stream()
                .flatMap(t -> new HashMap<>(Collections.singletonMap(t.getKey(), t.getValue().get(0))).entrySet().stream()).collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue(), (k1, k2) -> k1));

        // List<Map<String, String>> -> Map<String, List<Map<String, String>> -> Map<String, String>
        list.add(new UserModel(13, "Zao1", 29.51, "pink1"));

        Map<Integer, List<UserModel>> mmmmmap = list.stream().collect(Collectors.groupingBy(UserModel::getAge));

        List<Map<Integer, String>> lllllx = mmmmmap.entrySet().stream()
                .map(ma -> new HashMap<>(Collections.singletonMap(ma.getKey(), ma.getValue().stream().map(UserModel::getName).collect(Collectors.joining("\n")))))
                .collect(Collectors.toList());

        List<String> llllly = mmmmmap.entrySet().stream()
                .map(ma -> ma.getValue().stream().map(UserModel::getName).collect(Collectors.joining("\n")))
                .collect(Collectors.toList());

//        Map<Object, Object> lllllz = mmmmmap.entrySet().stream()
//                .map(ma -> ma.getValue().stream().map(UserModel::getName).collect(Collectors.joining("\n")))
//                .collect(Collectors.toMap(t -> t, v -> v, (k1, k2) -> k1));

        Map<Integer, String> lllll = mmmmmap.entrySet().stream()
                .map(ma -> new HashMap<>(Collections.singletonMap(ma.getKey(), ma.getValue().stream().map(UserModel::getName).collect(Collectors.joining("\n")))))
                .flatMap(t -> t.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k1));
        System.out.println();


        List<Map<String, Object>> listTest = new ArrayList<>();
        Map<String, Object> mm1 = new HashMap<>();mm1.put("id", "1");mm1.put("content", "c1");
        Map<String, Object> mm2 = new HashMap<>();mm2.put("id", "2");mm2.put("content", "c2");
        Map<String, Object> mm3 = new HashMap<>();mm3.put("id", "3");mm3.put("content", "c3");
        Map<String, Object> mm4 = new HashMap<>();mm4.put("id", "1");mm4.put("content", "c4");

        listTest.add(mm1);
        listTest.add(mm2);
        listTest.add(mm3);
        listTest.add(mm4);
        Map<String, List<Map<String, Object>>> map2Map = listTest.stream().collect(Collectors.groupingBy(t -> t.get("id").toString()));
        List<Map<String, String>> ss = map2Map.entrySet().stream()
                .map(t -> new HashMap<>(Collections.singletonMap(t.getKey(), t.getValue().stream().map(tt -> tt.get("content").toString()).collect(Collectors.joining("\n")))))
                .collect(Collectors.toList());
        Map<String, String> ssss = ss.stream().flatMap(t -> t.entrySet().stream()).collect(Collectors.toMap(t -> t.getKey(), t -> t.getValue(), (a, b) -> a));
        System.out.println(ssss);
        // flatMap可以理解为多个 Map 也就是 List<Map>的情况下 进行合并，最后合并为一个Map (对于重复的Key 只选择其中一个 toMap具体决定)

        // List<Map<String, Object>>  -> Map<String, Map<String, Object>>
        Map<String, Map<String, Object>> map2Map2 = listTest.stream().collect(Collectors.toMap(k -> k.get("id").toString(), v -> v, (k1, k2) -> k1));
        System.out.println(map2Map2);
    }
}
