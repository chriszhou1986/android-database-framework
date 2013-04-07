package com.wu.databasedemo.db.exception;

public class TableNameInvalidException extends SQLiteException {

	private static final long serialVersionUID = 4988533356861326817L;

	public TableNameInvalidException(String message) {
		super(message);
	}

}
