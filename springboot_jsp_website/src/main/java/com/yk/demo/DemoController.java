package com.yk.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/demo")
public class DemoController
{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @RequestMapping("/welcome")
    @Transactional
    public String welcome()
    {
        jdbcTemplate.query("SELECT * FROM t_s_role ", resultSet ->
        {
            while (resultSet.next())
            {
                String string1 = resultSet.getString(1);
                String string2 = resultSet.getString(2);
            }
        });
        
        CurParameterizedType type = new CurParameterizedType(Map.class, new Type[]{String.class, Object.class});
        
        List<HashMap> ret = jdbcTemplate.query("SELECT * FROM t_s_role ", new BeanPropertyRowMapper<>(HashMap.class));
        System.out.println();
        int count = jdbcTemplate.update("DELETE FROM t_s_role WHERE id = ?", "1");
        try
        {
            int result = jdbcTemplate.update("UPDATE t_s_role SET `name` = ? WHERE id = ?", "AAA", "2");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        return "view";
    }
    
    public static class CurParameterizedType implements ParameterizedType
    {
        private Class<?> clazz;
        
        private Type[] types;
        
        public CurParameterizedType(Class<?> clazz, Type[] types)
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
}
