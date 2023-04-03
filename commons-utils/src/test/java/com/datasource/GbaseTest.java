package com.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
     */
    public static void importData(DataSource dataSource,
                                  String type,
                                  String user,
                                  String passwd,
                                  String ip,
                                  String path,
                                  String table,
                                  String delimiter,
                                  String enclosed,
                                  String terminated)
    {
        PreparedStatement statement = null;
        StringBuilder sb = new StringBuilder();
        try (Connection connection = dataSource.getConnection())
        {
            logger.info("import data begin");
            sb.append("LOAD DATA CONCURRENT INFILE '").append(type).append("://").append(user).append(":")
                    .append(passwd).append("@").append(ip).append(path).append("' ");
            sb.append("REPLACE into TABLE ").append(table).append(" ");
            sb.append("CHARACTER SET utf8 ");
            sb.append("FIELDS TERMINATED BY '").append(delimiter).append("' ");
            sb.append("ENCLOSED BY '").append(enclosed).append("' ");
            sb.append("LINES TERMINATED BY '").append(terminated).append("';");
            String sql = sb.toString();
            logger.info("import data begin,  sql  is {}", sql);
            statement = connection.prepareStatement(sql);
            statement.execute();
        }
        catch (Exception e)
        {
            logger.error("gbase importData {} error", sb, e);
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
        }
    }

    public static void main(String[] args) throws SQLException
    {
        DruidDataSource dataSource = new DruidDataSource();
       /* dataSource.setDriverClassName("com.gbase.jdbc.Driver");
        dataSource.setUrl("jdbc:gbase://192.168.31.19:5258/test_one");
        dataSource.setUsername("gbase");
        dataSource.setPassword("gbase20110531");*/

        dataSource.setDriverClassName("com.gbase.jdbc.Driver");
        dataSource.setUrl("jdbc:gbase://192.170.24.41:5258");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

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
        /*System.out.println(System.currentTimeMillis());
        Connection conn = dataSource.getConnection();
        PreparedStatement statement = conn.prepareStatement("select `name`,`address`,`age` from test01 where (`name` is not null and `name`<> '')  or (`address` is not null and `address`<> '')  or (`age` is not null and `age`<> '')  limit 0,5");
        ResultSet _rs = statement.executeQuery();
        while (_rs.next())
        {
            String col1 = _rs.getString(1);
            String col2 = _rs.getString(2);
            String col3 = _rs.getString(3);
        }*/
        GbaseTest.importData(dataSource,
                "ftp",
                "yangkai",
                "Spinfo@0123",
                "192.190.20.252",
                "/data_1000_1.csv",
                "gclusterdb.data_100w_1_yangkai",
                ",", "\"", "\r\n");
        System.out.println(System.currentTimeMillis());
    }

    private static void query(DruidDataSource dataSource) throws SQLException
    {
        String sql = "SELECT  TABLE_SCHEMA,TABLE_NAME,COLUMN_NAME,DATA_TYPE, IFNULL(CHARACTER_MAXIMUM_LENGTH,0) AS DATA_LENGTH,COLUMN_COMMENT AS COMMENTS,ORDINAL_POSITION AS COLUMN_ID "
                + " FROM INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA=? and TABLE_NAME=? ORDER BY ORDINAL_POSITION";
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "test_one");
        statement.setString(2, "data_1000w_3");
        ResultSet _rs = statement.executeQuery();
        while (_rs.next())
        {
            String tableName = _rs.getString("COLUMN_NAME");
            System.out.println(tableName);
        }
        System.out.println();
    }
}
