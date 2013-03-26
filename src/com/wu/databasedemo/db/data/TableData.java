package com.wu.databasedemo.db.data;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Define table's parameters.
 * 
 * @author Administrator
 * 
 */
public class TableData {

	private String tableName;

	private String natrualKey;

	/** the key is column in entity table, the value is entity's field */
	private Map<String, Field> columnFields;

	public TableData() {
		super();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getNatrualKey() {
		return natrualKey;
	}

	public void setNatrualKey(String natrualKey) {
		this.natrualKey = natrualKey;
	}

	public Map<String, Field> getColumnFields() {
		return columnFields;
	}

	public void setColumnFields(Map<String, Field> columnFields) {
		this.columnFields = columnFields;
	}

	@Override
	public String toString() {
		return "TableData [tableName=" + tableName + ", natrualKey="
				+ natrualKey + ", columnFields=" + columnFields + "]";
	}

}
