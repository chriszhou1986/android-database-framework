package com.wu.databasedemo.db.data;

import java.lang.reflect.Field;

/**
 * @hide
 */
public final class FieldData {

	public String column;

	public Field field;

	@Override
	public String toString() {
		return "FieldData [column=" + column + ", field=" + field + "]";
	}

}
