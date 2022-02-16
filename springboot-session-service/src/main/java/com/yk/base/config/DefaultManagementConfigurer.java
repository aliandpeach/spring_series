package com.yk.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * annotationDrivenTransactionManager方法返回的就是全局的默认事务管理器
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/11 11:59:04
 */
@Component
public class DefaultManagementConfigurer implements TransactionManagementConfigurer
{
    @Autowired
    @Qualifier("commonTransactionManager")
    private PlatformTransactionManager commonTransactionManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public TransactionManager annotationDrivenTransactionManager()
    {
        return transactionManager;
    }
}
