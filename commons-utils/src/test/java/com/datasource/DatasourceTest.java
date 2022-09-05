package com.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.datasource.transaction.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static cn.hutool.core.thread.ThreadUtil.sleep;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/16 11:45:44
 */
public class DatasourceTest
{
    public DataSource create() throws Exception
    {
        Properties props = new Properties();
        props.put("useSSL", "false");
        props.put("useUnicode", "true");
        props.put("characterEncoding", "UTF-8");
        props.put("characterSetResults", "UTF-8");

        Properties properties = new Properties();
        properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("url", "jdbc:mysql://192.190.20.252:13307/one?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowMultiQueries=true");
        properties.setProperty("username", "test_one");
        properties.setProperty("password", "Spinfo@0123");
        properties.setProperty("dbType", "mysql");

        DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        druidDataSource.setBreakAfterAcquireFailure(true);
        druidDataSource.setConnectionErrorRetryAttempts(0);
        druidDataSource.setValidationQuery("select 1");
        druidDataSource.setConnectProperties(props);
        return druidDataSource;
    }

    public DataSource dataSource() throws SQLException
    {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.190.20.251:3306/test?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowMultiQueries=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDbType("mysql");

//        dataSource.setDriverClassName("com.pivotal.jdbc.GreenplumDriver");
//        dataSource.setUrl("jdbc:pivotal:greenplum://192.170.24.41:55432;DatabaseName=sdm_source");
//        dataSource.setUsername("gpadmin");
//        dataSource.setPassword("gpadmin");
//        dataSource.setDbType("mysql");

//        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
//        dataSource.setUrl("jdbc:oracle:thin:@192.190.50.19:1521:orcl");
//        dataSource.setUsername("TEST_TWO");
//        dataSource.setPassword("Spinfo#0123");
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
        // 用来检测连接是否有效的sql，要求是一个查询语句
//        dataSource.setValidationQuery("SELECT version()");
        // 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        dataSource.setTestWhileIdle(true);
        // 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        dataSource.setTestOnBorrow(false);
        // 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        dataSource.setTestOnReturn(false);
        // 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
        dataSource.setPoolPreparedStatements(false);
        // 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(0);
        // 配置监控统计拦截的filters，去掉后监控界面sql无法统计
        dataSource.setFilters("stat,wall");
        // 通过connectProperties属性来打开mergeSql功能；慢SQL记录
        dataSource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500");
        // 合并多个DruidDataSource的监控数据
        dataSource.setUseGlobalDataSourceStat(true);

//        dataSource.setRemoveAbandoned(true);
//        dataSource.setRemoveAbandonedTimeout(1800);
        dataSource.setLogAbandoned(true);
        dataSource.init();
        return dataSource;
    }

    public void insert(DataSource dataSource, int index) throws SQLException
    {
        DataSourceTransactionManager dataSourceTransactionManager = null;

        System.out.println("===========index===========：" + index);
        System.out.println("最大连接数：" + ((DruidDataSource) dataSource).getMaxActive());
        System.out.println("最小连接数：" + ((DruidDataSource) dataSource).getMinIdle());
        System.out.println("当前总连接数：" + ((DruidDataSource) dataSource).getActiveCount() + ((DruidDataSource) dataSource).getPoolingCount());
        System.out.println("当前活跃连接数：" + ((DruidDataSource) dataSource).getActiveCount());
        System.out.println("当前闲置连接数：" + ((DruidDataSource) dataSource).getPoolingCount());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO \"data_test_insert\" (\"id\", \"name\", \"index\") VALUES (?,?,?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                /*PreparedStatement statement2 = connection.prepareStatement("INSERT INTO \"data_test_insert\" (\"id\", \"name\", \"index\") VALUES (?,?,?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);*/)
        {
            dataSourceTransactionManager = new DataSourceTransactionManager(connection);
            connection.setAutoCommit(false);

            long start = System.currentTimeMillis();
            dataSourceTransactionManager.beginTransaction();
            for (int i = 0; i < 10000; i++)
            {
                statement.setString(1, UUID.randomUUID().toString().replace("-", ""));
                statement.setString(2, "name" + i);
                statement.setInt(3, index);
                statement.addBatch();
            }
            statement.executeBatch();
            System.out.println("time = " + (System.currentTimeMillis() - start));

//            if (index == 5)
//            {
//                statement2.setString(1, UUID.randomUUID().toString());
//                statement2.setString(2, "NAME");
//                statement2.setInt(3, index);
//                statement2.execute();
//            }

            dataSourceTransactionManager.commit();
        }
        catch (Exception e)
        {
            if (null != dataSourceTransactionManager)
            {
                dataSourceTransactionManager.rollback();
            }
            e.printStackTrace();
        }
//        finally
//        {
//            closeStatement(statement2);
//            closeStatement(statement);
//            doCloseConnection(connection, dataSource);
//        }
    }

    /**
     * 测试批量更新（表名不一样的场景, 由于PreparedStatement需要预编译, 不能把表名作为参数传入, 所以不行）
     * 改用createStatement的addBatch
     */
    public static void main(String[] args) throws SQLException
    {
        DatasourceTest datasourceTest = new DatasourceTest();
        DataSource dataSource = null;
        try
        {
            dataSource = datasourceTest.dataSource();
        }
        catch (Exception throwables)
        {
            throwables.printStackTrace();
        }

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM test_1");
             PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM test_2");
        )
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getString("id"));
                map.put("name", rs.getString("name"));
                map.put("tableName", "test_1");
                list.add(map);
            }
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next())
            {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs2.getString("id"));
                map.put("name", rs2.getString("name"));
                map.put("tableName", "test_2");
                list.add(map);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        )
        {
            for (Map<String, Object> e : list)
            {
                String _sql = "UPDATE " + e.get("tableName") + " set name = '" + e.get("name").toString() + "_" + e.get("id") + "' where id = " + e.get("id").toString();
                System.out.println(_sql);
                statement.addBatch(_sql);
            }
            statement.executeBatch();
        }
        System.out.println();
    }

    public static void main4(String[] args) throws SQLException
    {
        DatasourceTest datasourceTest = new DatasourceTest();
        DataSource dataSource = null;
        try
        {
            dataSource = datasourceTest.dataSource();
        }
        catch (Exception throwables)
        {
            throwables.printStackTrace();
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("select a.owner, a.index_name, a.uniqueness, b.column_name,b.table_name from  dba_indexes a, dba_ind_columns b where a.index_name = b.index_name and a.owner in (?) and  a.table_name = ? and a.owner = b.index_owner   order by b.column_position", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        )
        {
            System.out.println(Arrays.stream("TEST_TWO".trim().split(",")).collect(Collectors.joining("','", "'", "'")));
            ps.setString(1, Arrays.stream("TEST_TWO".trim().split(",")).collect(Collectors.joining(",")));
            ps.setString(2, "DATA_1000W_1");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                Map<String, Object> map = new HashMap<>();
                map.put("1", rs.getString("index_name"));
                map.put("2", rs.getString("column_name"));
                map.put("3", rs.getString("table_name"));
                map.put("4", rs.getString("uniqueness").equals("UNIQUE"));
                map.put("5", rs.getString("owner"));
                System.out.println(map);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void main2(String[] args) throws SQLException
    {
        DatasourceTest datasourceTest = new DatasourceTest();
        final DataSource dataSource = datasourceTest.dataSource();
        ExecutorService service = Executors.newFixedThreadPool(5);
//        datasourceTest.insert(dataSource, 999999);
        for (int index = 1; index <= 1000; index++)
        {
            final int i = index;
            service.execute(() ->
            {
                try
                {
                    datasourceTest.insert(dataSource, i);
                }
                catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                }
            });
        }
    }

    public static void main3(String[] args)
    {
        /*try
        {
            DatasourceTest datasourceTest = new DatasourceTest();
            DataSource dataSource = datasourceTest.dataSource();

            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("insert into test (`id`) values (?)");
            ps.setString(1, System.currentTimeMillis() + "_A");
            ps.execute();
            ps.close();
//            connection.close();
            sleep(120 * 1000);

//            connection = dataSource.getConnection();
            PreparedStatement _ps = connection.prepareStatement("insert into test (`id`) values (?)");
            _ps.setString(1, System.currentTimeMillis() + "_B");
            _ps.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        final Runtime r = Runtime.getRuntime();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    sleep(15000);
                    System.out.println("maxMemory = " + (r.maxMemory() / (1024 * 1024))
                            + ", totalMemory = " + (r.totalMemory() / (1024 * 1024))
                            + ", freeMemory = " + (r.freeMemory() / (1024 * 1024)));
                }
            }
        });
        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                DatasourceTest datasourceTest = new DatasourceTest();
                DataSource dataSource = null;
                try
                {
                    dataSource = datasourceTest.create();
                }
                catch (Exception throwables)
                {
                    throwables.printStackTrace();
                }
                try (Connection connection = dataSource.getConnection();
                     PreparedStatement ps = connection.prepareStatement("SELECT * FROM t_name_f0", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                )
                {
                    long start = System.currentTimeMillis();
//                    Statement ps = connection.createStatement();
                    ps.setFetchSize(Integer.MIN_VALUE);
                    ResultSet set = ps.executeQuery();
//                    set.setFetchSize(100);
                    System.out.println((System.currentTimeMillis() - start));

                    AtomicLong lo = new AtomicLong();
                    ResultSetMetaData _meta = set.getMetaData();
                    int _columnCount = _meta.getColumnCount();
                    while (set.next())
                    {
                        Map<String, Object> map = new HashMap<>();
                        for (int i = 1; i <= _columnCount; i++)
                        {
                            String a = set.getString(i);
                            map.put(_meta.getColumnLabel(i), a);
                        }
                        ((DruidPooledConnection) set.getStatement().getConnection()).setConnectedTimeNano(System.nanoTime());
                        lo.incrementAndGet();
                        Thread.sleep(500 * 1000);
                    }
                    System.out.println("  =============    " + lo.get());
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void closeStatement(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {
            }
            catch (Throwable ex)
            {
            }
        }
    }

    public static void doCloseConnection(Connection con, DataSource dataSource) throws SQLException
    {
        if (null != con)
        {
            con.close();
        }
    }
}