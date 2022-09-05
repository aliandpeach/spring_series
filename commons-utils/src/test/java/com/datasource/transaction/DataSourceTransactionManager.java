package com.datasource.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库事务管理器
 *
 * <p>封装数据库事务处理</p>
 *
 * 示例：<br>
 * Dialect dialect = ...;<br>
 * Connection conn = ...;<br>
 *
 * DataSourceTransactionManager tm = new DataSourceTransactionManager(dialect, conn);<br>
 * tm.beginTransaction();<br>
 *
 * try {<br>
 * update or delete table records ...<br>
 * tm.commit();<br>
 * }catch(Exception ex) {<br>
 * tm.rollback();<br>
 * }<br>
 */
public class DataSourceTransactionManager implements TransactionManager
{

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

    private Connection connection;

    public DataSourceTransactionManager(Connection connection)
    {
        this.connection = connection;
    }

    @Override
    public void beginTransaction() throws TransactionException
    {
        connectionHolder.set(connection);
    }

    @Override
    public void commit() throws TransactionException
    {
        Connection connection = connectionHolder.get();
        try
        {
            connection.commit();
        }
        catch (SQLException ex)
        {
            throw new TransactionException(ex);
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException ex)
                {
                }
            }
            connectionHolder.remove();
        }
    }

    @Override
    public void rollback() throws TransactionException
    {
        Connection connection = connectionHolder.get();
        try
        {
            connection.rollback();
        }
        catch (SQLException ex)
        {
            throw new TransactionException(ex);
        }
        finally
        {
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException ex)
                {
                }
            }
            connectionHolder.remove();
        }
    }

    public Connection getConnection()
    {
        return connection;
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

}
