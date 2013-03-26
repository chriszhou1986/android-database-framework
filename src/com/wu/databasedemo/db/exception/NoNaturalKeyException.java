package com.wu.databasedemo.db.exception;

public class NoNaturalKeyException extends SQLiteException {

	private static final long serialVersionUID = 7123656769518933569L;

	public NoNaturalKeyException(String message) {
		super(message);
	}

}
