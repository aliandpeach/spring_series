//package com.yk.db.jpa.support;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.TransactionManager;
//import org.springframework.transaction.annotation.TransactionManagementConfigurer;
//
///**
// * 描述
// *
// * @author yangk
// * @version 1.0
// * @since 2021/11/11 11:59:04
// */
//@Component
//public class MyTransactionManagementConfigurer implements TransactionManagementConfigurer
//{
//    @Autowired
//    @Qualifier("jpaTx")
//    private PlatformTransactionManager transactionManager;
//
//    @Override
//    public TransactionManager annotationDrivenTransactionManager()
//    {
//        // 返回的PlatformTransactionManager就表示这是默认的事务处理器，这样在Transactional注解上就不需要声明是使用哪个事务管理器;
//        return transactionManager;
//    }
//}
