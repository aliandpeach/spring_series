package com.datasource.transaction;

public interface TransactionManager {
	
	public void beginTransaction() throws TransactionException;

	public void commit() throws TransactionException;
	
	public void rollback() throws TransactionException;
	
}
