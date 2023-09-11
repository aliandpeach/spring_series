package com.test;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YouHuaTest
{
    @Test
    public void test()
    {
        //查学院分配列表
        List<Map<String, Object>> deptList = new ArrayList<>();
        //查学院、专业分配列表
        List<Map<String, Object>> majorList = new ArrayList<>();
        //查学院、专业、班级分配列表
        List<Map<String, Object>> classList = new ArrayList<>();
        //组装数据
        //组装学院、专业数据
        int i = 1;
        int j = 1;
        for (Map<String, Object> d : deptList)
        {
            d.put("id", i);//设置id,前端展示使用
            for (Map<String, Object> m : majorList)
            {
                if (d.get("setId").equals(m.get("setId")) && d.get("dorm_grade").equals(m.get("dorm_grade"))
                        && d.get("collegeId").equals(m.get("collegeId")))
                {
                    m.put("id", String.valueOf(d.get("id")) + j);//设置id,前端展示使用
                    if (d.get("children") == null)
                    {
                        List<Map<String, Object>> childrenList = new ArrayList<>();
                        childrenList.add(m);
                        d.put("children", childrenList);
                    }
                    else
                    {
                        List childrenList = JSONObject.parseArray(JSONObject.toJSONString(d.get("children")));
                        childrenList.add(m);
                        d.put("children", childrenList);
                    }
                }
                j++;
            }
            i++;
        }
        //组装专业、班级数据
        int k = 1;
        for (Map<String, Object> m : majorList)
        {
            for (Map<String, Object> c : classList)
            {
                if (m.get("setId").equals(c.get("setId")) && m.get("dorm_grade").equals(c.get("dorm_grade"))
                        && m.get("collegeId").equals(c.get("collegeId"))
                        && m.get("majorId").equals(c.get("majorId")))
                {
                    c.put("id", String.valueOf(m.get("id")) + k);//设置id,前端展示使用
                    if (m.get("children") == null)
                    {
                        List<Map<String, Object>> childrenList = new ArrayList<>();
                        childrenList.add(c);
                        m.put("children", childrenList);
                    }
                    else
                    {
                        List childrenList = JSONObject.parseArray(JSONObject.toJSONString(m.get("children")));
                        childrenList.add(c);
                        m.put("children", childrenList);
                    }
                }
                k++;
            }
        }

        Map<String, Map<String, Object>> _magorMap = majorList.stream()
                .collect(Collectors.toMap(t -> t.get("setId").toString() + t.get("dorm_grade").toString() + t.get("collegeId").toString() + t.get("majorId").toString(), t -> t, (k1, k2) -> k1));

        Map<String, Map<String, Object>> _classMap = majorList.stream()
                .collect(Collectors.toMap(t -> t.get("setId").toString() + t.get("dorm_grade").toString() + t.get("collegeId").toString() + t.get("majorId").toString(), t -> t, (k1, k2) -> k1));


        for (Map.Entry<String, Map<String, Object>> entry : _classMap.entrySet())
        {
            if (null == _magorMap.get(entry.getKey()))
            {
                continue;
            }
            Map<String, Object> value = _magorMap.get(entry.getKey());
            entry.getValue().put("id", String.valueOf(value.get("id")) + k);//设置id,前端展示使用
            if (value.get("children") == null)
            {
                List<Map<String, Object>> childrenList = new ArrayList<>();
                childrenList.add(entry.getValue());
                value.put("children", childrenList);
                continue;
            }
            List childrenList = JSONObject.parseArray(JSONObject.toJSONString(value.get("children")));
            childrenList.add(entry.getValue());
            value.put("children", childrenList);
        }
    }
}
