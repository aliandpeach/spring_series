package com.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yk.httprequest.JSONUtil;
import org.junit.Test;

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
    public void test()
    {
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
    }
}
