package com.datasource.transaction;

import java.sql.SQLException;

public class TransactionException extends SQLException {

	private static final long serialVersionUID = -1851175634580328103L;
	
	public TransactionException(String message) {
		super(message);
	}
	
	public TransactionException(Throwable cause) {
		super(cause);
	}
	
	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}

}
