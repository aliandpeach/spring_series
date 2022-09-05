package com.yk.demo.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.yk.base.uitl.SpringContext;
import com.yk.demo.dao.DbDAO;
import com.yk.demo.dao.TemplateDAO;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/24 14:30:08
 */
public class DbSearch
{
    private static final Logger logger = LoggerFactory.getLogger(DbSearch.class);

    private DbDAO dbDAO;

    private TemplateDAO templateDAO;

    private DataSource dataSource;

    public DbSearch(JdbcTemplate jdbcTemplate, DataSource dataSource)
    {
        this.dataSource = dataSource;
        templateDAO = new TemplateDAO(jdbcTemplate);
        dbDAO = SpringContext.getInstance().getBean(DbDAO.class);
        SqlSessionTemplate sqlSessionTemplate = SpringContext.getInstance().getBean(SqlSessionTemplate.class);
        try
        {
            for (Class<?> _c = dbDAO.getClass(); _c != Object.class; _c = _c.getSuperclass())
            {
                logger.info("db search reflect info!! c = {}", _c);
                Field _f = getField(_c, "sqlSessionTemplate");
                _f = getField(_c, "sqlSession");
                if (null != _f)
                {
                    Object o = _f.get(_c);
                    logger.error("db search reflect object {}", o.toString());
                }
            }
            DataSource _dataSource = sqlSessionTemplate.getConfiguration().getEnvironment().getDataSource();
            if (_dataSource instanceof DruidDataSource)
            {
                DruidDataSource _druidDataSource = ((DruidDataSource) _dataSource);
                Set<DruidPooledConnection> _set = _druidDataSource.getActiveConnections();
                logger.info("db search reflect info!! activeConnections count {}", _set.size());
            }
        }
        catch (Exception e)
        {
            logger.error("db search reflect error!!", e);
        }
    }

    public void doExecTemplate()
    {
        activeConnectionCalc();
        int r = new Random().nextInt(100000) + 100;
        Map<String, Object> param = new HashMap<>();
        param.put("id", r);
        param.put("name", "TEST_GROUP" + r + "-jdbc");
        templateDAO.insert(param);

        activeConnectionCalc();
        logger.info("jdbc query result {}", templateDAO.query().size());
        ExecutorService service = Executors.newFixedThreadPool(1);
        CountDownLatch latch = new CountDownLatch(1);
        service.execute(() ->
        {
            try
            {
                activeConnectionCalc();
                int rr = new Random().nextInt(100000) + 100;
                Map<String, Object> pp = new HashMap<>();
                pp.put("id", rr);
                pp.put("name", "TEST_GROUP" + rr + "-jdbc-thread");
                templateDAO.insert(pp);
                activeConnectionCalc();
                logger.info("主键重复, 在线程中无事务, 不回退, 上一条成功插入数据");
                templateDAO.insert(pp);
            }
            finally
            {
                latch.countDown();
            }

        });
        activeConnectionCalc();
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
        }
        activeConnectionCalc();
        // 主键重复, 事务回退
        templateDAO.insert(param);
    }

    private Field getField(Class<?> _c, String fieldName)
    {
        try
        {
            return _c.getField(fieldName);
        }
        catch (Exception e)
        {
            logger.error("db search reflect get field {} error {} message {}", fieldName, _c.getName(), e.getMessage());
            return null;
        }
    }

    public void doExec()
    {
        int r = new Random().nextInt(100000) + 100;
        Map<String, Object> param = new HashMap<>();
        param.put("id", r);
        param.put("name", "TEST_GROUP" + r);
        dbDAO.insert(param);
        activeConnectionCalc();

        r = new Random().nextInt(100000) + 100;
        param = new HashMap<>();
        param.put("id", r);
        param.put("name", "TEST_GROUP" + r + "-jdbc");
        templateDAO.insert(param);
        activeConnectionCalc();

        List<Map<String, Object>> temp = dbDAO.query();
        logger.info("db search query count {}", temp.size());
        CountDownLatch latch = new CountDownLatch(1);

        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(() ->
        {
            long start = System.currentTimeMillis();
            try
            {
                while (System.currentTimeMillis() / 1000 - start / 1000 < 150)
                {
                    sleep(1);
                    activeConnectionCalc();
                }
                logger.debug("db search thread running!!");
                int rr = new Random().nextInt(100000) + 100;
                Map<String, Object> pp = new HashMap<>();
                pp.put("id", rr);
                pp.put("name", "TEST_GROUP" + rr + "-thread");
                dbDAO.insert(pp);
                activeConnectionCalc();

                rr = new Random().nextInt(100000) + 100;
                pp = new HashMap<>();
                pp.put("id", rr);
                pp.put("name", "TEST_GROUP" + rr + "-thread-jdbc");
                templateDAO.insert(pp);
                logger.info("主键重复, 在线程中无事务, 不回退, 上两条成功插入数据, 即使druid已经remove掉Connection, 也不影响上一条插入数据");
                dbDAO.insert(pp);
            }
            catch (Exception e)
            {
                logger.debug("Exception error {}", e.getMessage());
                throw e;
            }
            finally
            {
                latch.countDown();
            }
            long end = System.currentTimeMillis();
            logger.info("db search thread running time {}", (end - start) / 1000);
        });
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            logger.error("db search await error", e);
        }
    }

    private void activeConnectionCalc()
    {
        try
        {
            if (dataSource instanceof DruidDataSource)
                logger.info("while db search reflect info!! activeConnections count {}", ((DruidDataSource) dataSource).getActiveConnections().size());
        }
        catch (Exception e)
        {
            logger.debug("getActiveConnections error {}", e.getMessage());
        }
    }

    public List<Map<String, Object>> getMaps()
    {
        activeConnectionCalc();
        logger.debug("db search await over!!");
        List<Map<String, Object>> result = null;
        try
        {
            dbDAO = SpringContext.getInstance().getBean(DbDAO.class);
            int r = new Random().nextInt(100000) + 100;
            Map<String, Object> param = new HashMap<>();
            param.put("id", r);
            param.put("name", "TEST_GROUP" + r);
            dbDAO.insert(param);

            int rr = new Random().nextInt(100000) + 100;
            Map<String, Object> pp = new HashMap<>();
            pp.put("id", rr);
            pp.put("name", "TEST_GROUP" + rr + "-jdbc");
            templateDAO.insert(pp);

            activeConnectionCalc();
            result = dbDAO.query();
            param = new HashMap<>();
            param.put("id", r);
            param.put("name", "TEST_GROUP" + r);
            dbDAO.insert(param);
        }
        catch (Exception e)
        {
            logger.error("db search query error", e);
            throw e; //没有抛出运行时异常的话, 事务是不生效的(第一条insert最后会插入到数据库中)
        }
        return result;
    }

    private void sleep(int second)
    {
        try
        {
            TimeUnit.SECONDS.sleep(second);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
