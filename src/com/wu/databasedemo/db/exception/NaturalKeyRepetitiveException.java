package com.wu.databasedemo.db.exception;

public class NaturalKeyRepetitiveException extends SQLiteException {

	private static final long serialVersionUID = 4689090944838364511L;

	public NaturalKeyRepetitiveException(String message) {
		super(message);
	}

}
