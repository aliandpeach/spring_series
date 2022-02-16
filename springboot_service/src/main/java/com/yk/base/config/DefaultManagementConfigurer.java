package com.yk.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * 对于同一个工程中存在多个事务管理器的处理
 *
 * 实现的annotationDrivenTransactionManager方法返回的就是全局默认的那个事务管理器
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/11 11:59:04
 */
@Component
public class DefaultManagementConfigurer implements TransactionManagementConfigurer
{
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    @Qualifier("commonTransactionManager")
    private PlatformTransactionManager commonTransactionManager;

    @Override
    public TransactionManager annotationDrivenTransactionManager()
    {
        // 返回的PlatformTransactionManager就表示这是默认的事务处理器，这样在Transactional注解上就不需要声明是使用哪个事务管理器;
        return transactionManager;
    }
}
