package com.demo.lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/09/10 14:28:58
 */
public class MainJ
{
    public static void main(String[] args)
    {
        List<QuotaVo> allList = new ArrayList<>();
        QuotaVo v1 = new QuotaVo();
        v1.setId(145);
        v1.setPid(1);
        v1.setName("教学内容");
        allList.add(v1);
        QuotaVo v2 = new QuotaVo();
        v2.setId(146);
        v2.setPid(145);
        v2.setName("授课思路");
        allList.add(v2);
        QuotaVo v3 = new QuotaVo();
        v3.setId(147);
        v3.setPid(145);
        v3.setName("作业收发、批改");
        allList.add(v3);
        QuotaVo v4 = new QuotaVo();
        v4.setId(148);
        v4.setPid(145);
        v4.setName("辅导、答疑");
        allList.add(v4);
        QuotaVo v6 = new QuotaVo();
        v6.setId(150);
        v6.setPid(145);
        v6.setName("实现专业知识传授和思想价值引领的融合");
        allList.add(v6);
        QuotaVo v7 = new QuotaVo();
        v7.setId(135);
        v7.setPid(1);
        v7.setName("教学态度");
        allList.add(v7);
        QuotaVo v8 = new QuotaVo();
        v8.setId(136);
        v8.setPid(135);
        v8.setName("教师迟到情况");
        allList.add(v8);
        QuotaVo v9 = new QuotaVo();
        v9.setId(137);
        v9.setPid(135);
        v9.setName("教师早退情况");
        allList.add(v9);
        QuotaVo v10 = new QuotaVo();
        v10.setId(139);
        v10.setPid(1);
        v10.setName("教学基本功");
        allList.add(v10);

        QuotaVo v11 = new QuotaVo();
        v11.setId(140);
        v11.setPid(139);
        v11.setName("授课态度");
        allList.add(v11);
        QuotaVo v5 = new QuotaVo();
        v5.setId(149);
        v5.setPid(139);
        v5.setName("思政政治教育实践活动");
        allList.add(v5);


        List<QuotaVo> rest = allList
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(QuotaVo::getId))), ArrayList::new));

        Function<QuotaVo, Integer> fun1 = QuotaVo::getId;
        List<QuotaVo> sortedList = allList.stream().sorted(Comparator.comparing(fun1)).collect(Collectors.toList());

        Map<Integer, List<QuotaVo>> map = rest.stream().collect(Collectors.groupingBy(QuotaVo::getPid));

        List<QuotaVo> tops = map.get(1);

        List<QuotaVo> topRes = sort(map, tops);

        System.out.println(topRes);
    }

    public static List<QuotaVo> sort(Map<Integer, List<QuotaVo>> map, List<QuotaVo> currents)
    {
        List<QuotaVo> res = new ArrayList<>();
        for (QuotaVo node : currents)
        {
            QuotaVo top = node;

            List<QuotaVo> temp = map.get(node.getId());
            if (null != temp && temp.size() != 0)
            {
                Collections.sort(temp);
                node.setChildren(temp);
                sort(map, temp);
            }
            res.add(top);
        }
        return res;
    }
}
