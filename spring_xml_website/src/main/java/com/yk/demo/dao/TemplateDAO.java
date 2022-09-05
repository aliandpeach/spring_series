package com.yk.demo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/24 17:08:23
 */
public class TemplateDAO
{
    private JdbcTemplate jdbcTemplate;

    public TemplateDAO(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> query()
    {
        return jdbcTemplate.query("SELECT * FROM t_group", new RowMapper<Map<String, Object>>()
        {

            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();
                Map<String, Object> r = new HashMap<String, Object>();
                for (int i = 1; i <= count; i++)
                {
                    r.put(metaData.getColumnName(i), rs.getObject(metaData.getColumnName(i)));
                }
                return r;
            }
        });
    }

    public int insert(Map<String, Object> param)
    {
        return jdbcTemplate.update("INSERT INTO t_group (`id`, `name`) VALUES (?, ?)", new Object[]{param.get("id"), param.get("name")});
    }
}
