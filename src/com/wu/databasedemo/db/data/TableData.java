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

	private List<FieldData> foreignFields;

	private List<FieldData> columnFields;

	public TableData() {
		super();
		this.foreignFields = new ArrayList<FieldData>();
		this.columnFields = new ArrayList<FieldData>();
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

	public List<FieldData> getForeignFields() {
		return foreignFields;
	}

	public void setForeignFields(List<FieldData> foreignFields) {
		if (foreignFields == null) {
			this.foreignFields = new ArrayList<FieldData>();
		} else {
			this.foreignFields = foreignFields;
		}
	}

	public List<FieldData> getColumnFields() {
		return columnFields;
	}

	public void setColumnFields(List<FieldData> columnFields) {
		if (columnFields == null) {
			this.columnFields = new ArrayList<FieldData>();
		} else {
			this.columnFields = columnFields;
		}
	}

	public void addForeignField(String column, Field field) {
		if (column != null && field != null) {
			FieldData fieldData = new FieldData();
			fieldData.column = column;
			fieldData.field = field;
			this.foreignFields.add(fieldData);
		}
	}

	public void addColumnField(String column, Field field) {
		if (column != null && field != null) {
			FieldData fieldData = new FieldData();
			fieldData.column = column;
			fieldData.field = field;
			this.columnFields.add(fieldData);
		}
	}

	@Override
	public String toString() {
		return "TableData [tableName=" + tableName + ", naturalKey="
				+ naturalKey + ", foreignFields=" + foreignFields
				+ ", columnFields=" + columnFields + "]";
	}

}
