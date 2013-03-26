package com.wu.databasedemo.db.exception;

public class SQLiteException extends RuntimeException {

	private static final long serialVersionUID = -2915174810401903155L;

	public SQLiteException() {
		super();
	}

	public SQLiteException(String message) {
		super(message);
	}

	public SQLiteException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public SQLiteException(Throwable throwable) {
		super(throwable);
	}

}
