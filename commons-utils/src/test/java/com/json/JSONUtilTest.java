package com.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yk.httprequest.JSONUtil;
import lombok.Data;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * JSONUtilTest
 */
public class JSONUtilTest
{

    @Test
    public void test() throws IOException
    {
        String json_j = "{\"result\":{\"机密\":[{\"16\":2}]},\"code\":0,\"level\":\"alert\",\"message\":\"操作成功！\",\"taskId\":\"0a4c3acf-de76-4434-9afc-05b5940426c6\"}";
        TestModel result_r = JSONUtil.fromJson(json_j, new TypeReference<TestModel>()
        {
        });

        String ssssss = "userpass:";
        String[] ary = ssssss.split(":");

        List<TestUser> users = new ArrayList<>();
        Map<Integer, TestUser> mapUsers = new HashMap<>();
        
        Map<Integer, String> mapStrings = new HashMap<>();
        IntStream.range(0, 10).forEach((i) ->
        {
            users.add(new TestUser("name" + i, 20, "email" + i));
            
            mapUsers.put(i, new TestUser("name" + i, 20, "email" + i));
            
            mapStrings.put(i, i + "");
        });
        
        System.out.println(users);
        String json = JSONUtil.toJson(users);
        String mapuser_json = JSONUtil.toJson(mapUsers);
        Optional.of(json).ifPresent(System.out::println);
        JSONUtil.CurParameterizedType type = new JSONUtil.CurParameterizedType(List.class, new Type[]{TestUser.class});
        
        List<TestUser> _usersx = JSONUtil.fromJson(json, new TypeReference<List<TestUser>>()
        {
        });
        System.out.println(_usersx);

        // CurTypeReference2 这么写的话, 会造成List<>中的对象是LinkedHashMap而不是TestUser
        List<TestUser> __users = JSONUtil.fromJson(json, new JSONUtil.CurTypeReference2<List<TestUser>>());
        List<TestUser> _users = JSONUtil.fromJson(json, new JSONUtil.CurTypeReference<List<TestUser>>(type));
        System.out.println(_users);
        
        
        JSONUtil.CurParameterizedType _type = new JSONUtil.CurParameterizedType(Map.class, new Type[]{Integer.class, TestUser.class});
        Map<Integer, TestUser> _mapUsers = JSONUtil.fromJson(mapuser_json, new JSONUtil.CurTypeReference<Map<Integer, TestUser>>(_type));
        System.out.println(_mapUsers);
        
        Map<Integer, TestUser> __mapUsers = JSONUtil.fromJson(mapuser_json, new TypeReference<Map<Integer, TestUser>>()
        {
        });
        System.out.println(__mapUsers);
        
        JSONUtil.CurParameterizedType __type = new JSONUtil.CurParameterizedType(Map.class, new Type[]{Integer.class, String.class});
        Map<Integer, TestUser> _mapStrings = JSONUtil.fromJson(JSONUtil.toJson(mapStrings), new JSONUtil.CurTypeReference<Map<Integer, TestUser>>(__type));
        System.out.println(_mapStrings);
        
        
        TestUser user = new TestUser("mail", 21, "email");
        String j = JSONUtil.toJson(user);
        TestUser t = JSONUtil.fromJson(j, TestUser.class);
        TestUser t2 = JSONUtil.fromJson(j, new TypeReference<TestUser>()
        {
        });
        System.out.println(t2);


        String str1 = "{\n" +
                "    \"errcode\": 0,\n" +
                "    \"errmsg\": \"ok\",\n" +
                "    \"department\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"企业微信测试\",\n" +
                "            \"parentid\": 0,\n" +
                "            \"order\": 100000000\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 2,\n" +
                "            \"name\": \"A招生团队\",\n" +
                "            \"parentid\": 1,\n" +
                "            \"order\": 100000000\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 3,\n" +
                "            \"name\": \"B招生团队\",\n" +
                "            \"parentid\": 1,\n" +
                "            \"order\": 99999000\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 2777763842,\n" +
                "            \"name\": \"G招生团队\",\n" +
                "            \"parentid\": 1,\n" +
                "            \"order\": 99998000\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        JSONObject o = JSON.parseObject(str1);
        String de = o.getString("department");
        List<Map<String, Object>> depart1 = JSON.parseObject(de, new com.alibaba.fastjson.TypeReference<List<Map<String, Object>>>()
        {
        });


        // cn.hutool.json.JSONUtil不能解析复杂的json
//        List<Map<String, Object>> depart5 = cn.hutool.json.JSONUtil.toBean(de, new cn.hutool.core.lang.TypeReference<List<Map<String, Object>>>()
//        {
//        }, true);

        Gson gson = new Gson();
        List<Map<String, Object>> depart2 = gson.fromJson(de, new MyType<List<Map<String, Object>>>(List.class, new Type[]{Map.class, String.class, Object.class}));
        List<Map<String, Object>> depart3 = gson.fromJson(de, new TypeToken<List<Map<String, Object>>>(){}.getType());
    }

    private static class MyType<T> implements ParameterizedType
    {
        private final Class<?> clazz;

        private final Type[] types;

        public MyType(Class<?> clazz, Type[] types)
        {
            this.clazz = clazz;
            this.types = types;
        }

        public Type[] getActualTypeArguments()
        {
            return null == types ? new Type[0] : types;
        }

        public Type getRawType()
        {
            return clazz;
        }

        public Type getOwnerType()
        {
            return clazz;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TestModel
    {
        private String taskId;
        private Map<String, List<Map<Integer, Integer>>> result;
    }
}
