package com.yk.demo.service;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/24 14:30:08
 */
@Service
public class DbService
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    // Spring事务通过代理实现, 这里要注入自身, 才能在用this调用自身方法的时候使事务生效(也可以配置 expose-proxy="true"后, 通过AopContext.currentProxy() 调用方法)
//    @Autowired
//    private DbService dbService;

    // 事务作用期间, 会维持同一个Connection, 如果维持的时间大于druid回收Connection的时间, 则Connection被回收后再调用, 继续执行insert/update 则会报错
    @Transactional
    public void doExec()
    {
        DbSearch dbSearch = new DbSearch(jdbcTemplate, dataSource);
        ((DbService) AopContext.currentProxy()).doExecFirst(dbSearch);
        // 事务的隔离性, doExecFirst结束时, 会执行doCommit
        // (前提是没有在 doExec上加入事务, 不加的话doExecFirst和doExecNext是两个不同的事务, 各自会commit或者rollback)
        ((DbService) AopContext.currentProxy()).doExecNext(dbSearch);
    }

    @Transactional
    public void doExecFirst(DbSearch dbSearch)
    {
        dbSearch.doExec();
    }

    @Transactional
    public void doExecNext(DbSearch dbSearch)
    {
        dbSearch.getMaps();
    }

    @Transactional
    public void doExecTemplate()
    {
        DbSearch dbSearch = new DbSearch(jdbcTemplate, dataSource);
        dbSearch.doExecTemplate();
    }
}
