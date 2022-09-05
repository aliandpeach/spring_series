package com.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/28 16:00:11
 */
public class GbaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(GbaseTest.class);

    public static long exportData(DataSource dataSource,
                                  String sql,
                                  String delimiter,
                                  String filePath)
    {
        return 0;
    }

    /**
     * DROP TABLE tmp.my_table CASCADE;
     * CREATE TABLE tmp.my_table
     * ( field_1 varchar
     * , field_2 varchar
     * , field_3 varchar
     * , field_4 varchar
     * );
     *
     * COPY tmp.my_table (field_1,field_2,field_3,field_4)
     * FROM STDIN
     * WITH CSV DELIMITER ',' QUOTE '"' ESCAPE '\'
     * ;
     * "value","another value","this is \"another\" value","no more, thanks"
     * \.
     * ;
     *
     * @param dataSource
     * @param table
     * @param delimiter
     * @param file
     * @return
     */
    public static long importData(DataSource dataSource,
                                  String table,
                                  String delimiter,
                                  String file)
    {
        PreparedStatement statement = null;
        Connection con = null;
        try
        {
            logger.info("import data begin");
            con = dataSource.getConnection();
            StringBuffer sb = new StringBuffer();
            sb.append("LOAD DATA INFILE 'ftp://yangkai@192.190.20.252/yangkai/data_test_1.csv' \n");
            sb.append("REPLACE INTO TABLE " + table);
            String sql = sb.toString();
            logger.info("import data begin,  sql  is {}", sql);
            statement = con.prepareStatement(sql);
            statement.execute();
            return 0;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            return 0L;
        }
        finally
        {
            if (statement != null)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String[] args)
    {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.gbase.jdbc.Driver");
        dataSource.setUrl("jdbc:gbase://192.168.39.129:5258/bzcp_gbk");
        dataSource.setUsername("sysdba");
        dataSource.setPassword("Spinfo0123");
        dataSource.setDbType("mysql");
        //最大连接池数量
        dataSource.setMaxActive(100);
        // 初始化时建立物理连接的个数
        dataSource.setInitialSize(5);
        // 最小连接池数量
        dataSource.setMinIdle(5);
        // 获取连接时最大等待时间，单位毫秒
        dataSource.setMaxWait(60000);
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        // 连接保持空闲而不被驱逐的最小时间
        dataSource.setMinEvictableIdleTimeMillis(300000);
        System.out.println(System.currentTimeMillis());
        GbaseTest.importData(dataSource, "bzcp_gbk.data_1000_2", ",", "'ftp://yangkai@192.190.20.252/yangkai/data_test_1.csv'");
        System.out.println(System.currentTimeMillis());
    }
}
