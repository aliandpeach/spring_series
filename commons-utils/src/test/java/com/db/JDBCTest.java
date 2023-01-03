package com.db;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/09 10:39:43
 */
public class JDBCTest
{
    private ResultSet rs;

    @Test
    public void testReadLimit() throws Exception
    {
        JDBCTest jdbcTest = new JDBCTest();
        List<Map<String, Object>> r = jdbcTest.readSize(100);
        List<Map<String, Object>> r1 = jdbcTest.readSize(100);
        List<Map<String, Object>> r2 = jdbcTest.readSize(100);
        System.out.println("============================");
        jdbcTest.close();
    }

    private List<Map<String, Object>> read(int limit) throws Exception
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.31.205:3306/tmc_db?useUnicode=true&useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&serverTimezone=GMT%2B8", "root", "root");
        connection.setAutoCommit(false); //NOTE 为了设置fetchSize,必须设置为false
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT * FROM sys_alarm_history";
        PreparedStatement pstmt;
        try
        {
            pstmt = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            pstmt.setFetchSize(Integer.MIN_VALUE);
            pstmt.setFetchDirection(ResultSet.FETCH_REVERSE);
            System.out.println("ps.getQueryTimeout():" + pstmt.getQueryTimeout());
            System.out.println("ps.getFetchSize():" + pstmt.getFetchSize());
            System.out.println("ps.getFetchDirection():" + pstmt.getFetchDirection());
            System.out.println("ps.getMaxFieldSize():" + pstmt.getMaxFieldSize());

            if (null == rs || rs.isClosed())
            {
                rs = pstmt.executeQuery();
            }
            int col = rs.getMetaData().getColumnCount();// 表的column数量
            int count = 0;
            while (limit > 0 && rs.next())
            {
                Map<String, Object> data = new HashMap<>();
                for (int i = 1; i <= col; i++)
                {
                    data.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                }
                System.out.println(", count : " + ++count);
                result.add(data);
                limit--;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            //close resources
        }
        return result;
    }
    private List<Map<String, Object>> readSize(int fetchSize) throws Exception
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.31.205:3306/tmc_db?useUnicode=true&useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&serverTimezone=GMT%2B8", "root", "root");
        connection.setAutoCommit(false); //NOTE 为了设置fetchSize,必须设置为false
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT * FROM sys_alarm_history";
        PreparedStatement statement;
        try
        {
            statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(fetchSize);
            System.out.println("ps.getQueryTimeout():" + statement.getQueryTimeout());
            System.out.println("ps.getFetchSize():" + statement.getFetchSize());
            System.out.println("ps.getFetchDirection():" + statement.getFetchDirection());
            System.out.println("ps.getMaxFieldSize():" + statement.getMaxFieldSize());

            if (null == rs || rs.isClosed())
            {
                rs = statement.executeQuery();
            }
            int col = rs.getMetaData().getColumnCount();// 表的column数量
            int count = 0;
            while (fetchSize > 0 && rs.next())
            {
                Map<String, Object> data = new HashMap<>();
                for (int i = 1; i <= col; i++)
                {
                    data.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                }
                System.out.println(", count : " + ++count);
                result.add(data);
                fetchSize--;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            //close resources
        }
        return result;
    }

    private void close()
    {
        try
        {
            if (null != rs)
            {
                rs.close();
            }
        }
        catch (Exception e)
        {

        }
    }
}
