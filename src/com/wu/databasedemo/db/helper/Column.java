package com.wu.databasedemo.db.helper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Column {

	public ColumnClass classType() default ColumnClass.STRING;

	public enum ColumnClass {
		INTEGER, LONG, FLOAT, DOUBLE, STRING, BOOLEAN
	}

}
