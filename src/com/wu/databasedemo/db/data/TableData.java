package com.wu.databasedemo.db.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Define table's parameters.
 * 
 * @author Administrator
 * 
 */
public class TableData {

	private String tableName;

	private FieldData naturalKey;

	private List<FieldData> foreignColumns;

	/**
	 * The persistent column that is not foreign-key.
	 */
	private List<FieldData> normalColumns;

	public TableData() {
		super();
		this.foreignColumns = new ArrayList<FieldData>();
		this.normalColumns = new ArrayList<FieldData>();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public FieldData getNaturalKey() {
		return naturalKey;
	}

	public void setNaturalKey(FieldData naturalKey) {
		this.naturalKey = naturalKey;
	}

	public List<FieldData> getForeignColumns() {
		return foreignColumns;
	}

	public void setForeignColumns(List<FieldData> foreignColumns) {
		if (foreignColumns == null) {
			this.foreignColumns = new ArrayList<FieldData>();
		} else {
			this.foreignColumns = foreignColumns;
		}
	}

	public List<FieldData> getNormalColumns() {
		return normalColumns;
	}

	public void setNormalColumns(List<FieldData> normalColumns) {
		if (normalColumns == null) {
			this.normalColumns = new ArrayList<FieldData>();
		} else {
			this.normalColumns = normalColumns;
		}
	}

	public void addForeignColumn(String column, Field field) {
		if (column != null && field != null) {
			FieldData fieldData = new FieldData();
			fieldData.column = column;
			fieldData.field = field;
			this.foreignColumns.add(fieldData);
		}
	}

	public void addNormalColumn(String column, Field field) {
		if (column != null && field != null) {
			FieldData fieldData = new FieldData();
			fieldData.column = column;
			fieldData.field = field;
			this.normalColumns.add(fieldData);
		}
	}

	@Override
	public String toString() {
		return "TableData [tableName=" + tableName + ", naturalKey="
				+ naturalKey + ", foreignColumns=" + foreignColumns
				+ ", normalColumns=" + normalColumns + "]";
	}

}
