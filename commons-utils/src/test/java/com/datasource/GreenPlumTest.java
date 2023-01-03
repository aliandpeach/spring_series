package com.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/28 16:00:11
 */
public class GreenPlumTest
{
    private static final Logger logger = LoggerFactory.getLogger(GreenPlumTest.class);

    /**
     * @param dataSource
     * @param sql
     * @param delimiter
     * @param filePath
     * @param encode
     * @param header
     * @return
     */
    public static long exportData(DataSource dataSource,
                                  String sql,
                                  String delimiter,
                                  String filePath,
                                  String encode,
                                  boolean header)
    {
        Connection con = null;
        OutputStream out = null;
        Writer writer = null;
        try
        {
            con = dataSource.getConnection();
            CopyManager cm = new CopyManager(con.unwrap(BaseConnection.class));
            StringBuffer sb = new StringBuffer();
            sb.append("copy (");
            sb.append(sql);
            sb.append(" ) TO STDOUT ");
            sb.append("WITH DELIMITER '");
            sb.append(delimiter);
            sb.append("'");
            if (header)
            {
                sb.append(" HEADER ");
            }
            String copySql = sb.toString();
            logger.info("exportData data begin ,  sql  is {}", copySql);

            long startTime = System.currentTimeMillis();
            File file = new File(filePath);
            if (!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file);
            long handledRowCount = 0;
            if (StringUtils.isNotEmpty(encode))
            {
                writer = new OutputStreamWriter(out, encode);
                handledRowCount = cm.copyOut(copySql, writer);
            }
            else
            {
                handledRowCount = cm.copyOut(copySql, out);
            }
            long elapsedTime = System.currentTimeMillis() - startTime;

            logger.info("exportData data end, sql  is {}, elapsed time = {}", copySql, elapsedTime);
            return handledRowCount;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return 0L;
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                }
            }

            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException e)
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

    public static long importData(DataSource dataSource,
                                  String table,
                                  String delimiter,
                                  String quote,
                                  String file) throws SQLException, IOException
    {
        try (InputStream in = new FileInputStream(file);
             Connection con = dataSource.getConnection())
        {
            logger.info("import data begin");
            CopyManager cm = new CopyManager(con.unwrap(BaseConnection.class));
            StringBuffer sb = new StringBuffer();
            sb.append("copy ");
            sb.append(table);
            sb.append(" from STDIN ");
            sb.append(" WITH csv DELIMITER '");
            sb.append(delimiter);
            sb.append("'");
//            sb.append(" ENCODING 'UTF8'");
            sb.append(" QUOTE '").append(quote).append("'");
            String copySql = sb.toString();
            logger.info("import data begin,  sql  is {}", copySql);
            long startTime = System.currentTimeMillis();
            long handledRowCount = cm.copyIn(copySql, in);
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("import data end,  sql  is {}, elapsed time = {}", copySql, elapsedTime);
            return handledRowCount;
        }
        catch (IOException | SQLException e)
        {
            logger.error(e.getMessage(), e);
            throw e;
        }
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
                                  String file, String quote, String escape)
    {
        Connection con = null;
        InputStream in = null;
        try
        {
            logger.info("import data begin");
            con = dataSource.getConnection();
            CopyManager cm = new CopyManager(con.unwrap(BaseConnection.class));
            StringBuffer sb = new StringBuffer();
            sb.append("copy ");
            sb.append(table);
            sb.append(" from STDIN  ");
            sb.append("WITH DELIMITER '");
            sb.append(delimiter);
            sb.append("'");
            /*sb.append(" QUOTE '");
            sb.append(quote);
            sb.append("'");
            sb.append(" ESCAPE '");
            sb.append(escape);
            sb.append("'");*/
            String copySql = sb.toString();
            logger.info("import data begin,  sql  is {}", copySql);
            long startTime = System.currentTimeMillis();
            in = new FileInputStream(file);
            long handledRowCount = cm.copyIn(copySql, in);
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("import data end,  sql  is {}, elapsed time = {}", copySql, elapsedTime);
            return handledRowCount;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            return 0L;
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
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

    public static void main(String[] args) throws SQLException, IOException
    {
        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setDriverClassName("com.pivotal.jdbc.GreenplumDriver");
//        dataSource.setUrl("jdbc:pivotal:greenplum://192.170.24.45:5432;DatabaseName=postgres");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://192.170.24.45:5432/postgres?gssEncMode=disable");
        dataSource.setUsername("gpadmin");
        dataSource.setPassword("gpadmin");
//        dataSource.setDbType("oracle");
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
        GreenPlumTest.importData(dataSource, "public.\"data_1000_1_YangKai\"", ",", "\"", "C:\\Users\\Admin\\Desktop\\79934aca42b54ff7806b36162803cb21.csv");
        System.out.println(System.currentTimeMillis());
        /*try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("select * from data_1000w_1"))
        {
//            conn.setAutoCommit(false);
//            statement.setFetchSize(100);
            ResultSet rs = statement.executeQuery();
            while (rs.next())
            {
                String name = rs.getString("NAME");
                System.out.println(name);
            }
        }
        catch (Exception e)
        {

        }*/
    }
}
