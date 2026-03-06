package com.yk.test.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.SQLException;

/**
 * 此动态数据源主要用于不固定的数据库, 例如自动创建数据库导入数据, 然后传值数据库名切换datasource
 *
 * 若使用固定的数据库, 例如主备数据库, 切换动态数据源则可以使用AbstractRoutingDataSource实现
 * (AbstractRoutingDataSource 本身就是实现了javax.sql.DataSource,
 * 配置为AbstractRoutingDataSource-Bean, 在配置了mybatis的SqlSessionFactoryBean为Bean后会在afterPropertiesSet中传入AbstractRoutingDataSource-Bean,
 * mybatis在使用datasource时直接调用getConnection, 方法内会先调用determineTargetDataSource (determineCurrentLookupKey)方法, 因此自定义的AbstractRoutingDataSource实现类需要实现determineTargetDataSource方法
 * AbstractRoutingDataSource-Bean也需要再初始化时, 配置内部的Map, key为master和slave, value为master-datasource-bean 和 slave-datasource-bean
 * 若最后配置多个datasource-bean, 包括AbstractRoutingDataSource-Bean, master-datasource-bean, slave-datasource-bean, 则需要在AbstractRoutingDataSource-Bean上配置@Primary类标记为高优先级,
 * 其余的配置还需要有ThreadLocal和Aspect, 在Aspect中通过获取@Dynamic的参数拿到key, 再传入ThreadLocal,  determineTargetDataSource通过ThreadLocal拿到key, 进而AbstractRoutingDataSource内部通过key获取Map中的datasource)
 *
 * 结合AbstractRoutingDataSource的方法, 若我们需要动态使用一个ITestService的多个实现类, 那么就可以创建一个RoutingTestService-bean, 同样实现了ITestService接口,
 * 内部维护Map, 放入其他所有的实现类,  之后通过 ThreadLocal、Aspect、@Dynamic等实现
 * (在实际需要引入依赖的类中, 引入RoutingTestService-bean, 使用的如此便可实现在不同方法上增加不同value的注解 ======= 这个好像行不通待验证??????????)
 *
 *
 * https://blog.csdn.net/qq_43641418/article/details/127672988
 * https://www.cnblogs.com/chengxy-nds/p/17926002.html
 * https://cloud.tencent.com/developer/article/1710599
 *
 * 该配置适用于不固定的动态数据眼, 在@Dynamic注解的方法中, 第一个参数决定了最终使用哪个数据源
 */
public class DynamicDataSource extends DruidDataSource
{
    private static final long serialVersionUID = 4059357116837066441L;

    @Override
    public DruidPooledConnection getConnection() throws SQLException
    {
        try
        {
            return MyDruidConnectionHolder.getInstance().getConnection(this);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return getConnection(maxWait);
    }
}
