package com.wu.databasedemo.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.style.QuoteSpan;
import android.util.Log;

import com.wu.databasedemo.app.MyApplication;
import com.wu.databasedemo.db.helper.Column;
import com.wu.databasedemo.db.helper.Column.ColumnClass;
import com.wu.databasedemo.db.helper.NotPersistent;
import com.wu.databasedemo.db.helper.Table;
import com.wu.databasedemo.db.helper.TableData;

public class SQLiteOperator extends SQLiteOpenHelper implements ISQLiteOpenHelper {

	private static final int VERSION = 1;
	
	private static SQLiteOperator instance;

	private SQLiteOperator(Context context) {
		super(context, SQLiteManager.DATABASE_NAME, null, VERSION);
	}
	
	public static SQLiteOperator getInstance() {
		if (instance == null) {
			instance = new SQLiteOperator(MyApplication.Instance);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		SQLiteManager.getInstance();
		execBuildTablesSQL(db);
	}
	
	/**
	 * Create SQLite tables, execute CREATE TABLE SQL.
	 * 
	 * @param db
	 * @return If has exception during execute SQL, return false, otherwise
	 *         return true.
	 */
	private boolean execBuildTablesSQL(SQLiteDatabase db) {
		boolean result = true;
		for (int i = 0; i < SQLiteManager.ENTITY_PERSISTENT.length; i++) {
			Class<?> cl = SQLiteManager.ENTITY_PERSISTENT[i];

			String tableName = SQLiteManager.mEntityDBTables.get(cl).getTableName();
			Set<String> columns = SQLiteManager.mEntityDBTables.get(cl).getColumnFields().keySet();
			if (columns == null || columns.size() == 0) {
				continue;
			}
			
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE IF NOT EXISTS " + tableName.toUpperCase() + "(");
			sql.append(Constant.PRIMARY_KEY + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT");

			Iterator<String> it = columns.iterator();
			while (it.hasNext()) {
				sql.append(", " + it.next() + " TEXT");
			}
			sql.append(")");
			android.util.Log.i("test", "build table sql: " + sql);
			try {
				db.execSQL(sql.toString());
			} catch (SQLException e) {
				result = false;
				android.util.Log.e("test", "build table exception: " + e.toString());
			}
		}
		return result;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		SQLiteManager.getInstance();
		execDropTablesSQL(db);
		onCreate(db);
	}

	private void execDropTablesSQL(SQLiteDatabase db) {
		Collection<TableData> tables = SQLiteManager.mEntityDBTables.values();
		Iterator<TableData> it = tables.iterator();
		while (it.hasNext()) {
			TableData data = it.next();
			db.execSQL("DROP TABLE IF EXISTS " + data.getTableName());
		}
	}

	@Override
	public boolean save(Object object) {
		SQLiteManager.getInstance();
		if (object == null) {
			return false;
		}
		String tableName = SQLiteManager.mEntityDBTables.get(object.getClass()).getTableName();
		SQLiteDatabase db = getWritableDatabase();
		long row = db.insert(tableName, null, generateContentValues(object));
		android.util.Log.i("test", "save insert result: " + row);
		return false;
	}

	@Override
	public <T> boolean saveAll(List<T> objects) {
		SQLiteManager.getInstance();
		if (objects == null || objects.size() == 0) {
			return false;
		}
		boolean result = true;
		for (int i = 0; i < objects.size(); i++) {
			if (!save(objects.get(i))) {
				result = false;
			}
		}
		return result;
	}
	
	@Override
	public <T> boolean deleteById(Class<T> type, String id) {
		SQLiteManager.getInstance();
		SQLiteDatabase db = getWritableDatabase();
		TableData data = SQLiteManager.mEntityDBTables.get(type);
		String tableName = data.getTableName();
		String natrualKey = data.getNatrualKey();
		if (natrualKey == null || natrualKey.length() == 0) {
			natrualKey = Constant.PRIMARY_KEY;
		}
		int row = db.delete(tableName, natrualKey + "=?", new String[]{id});
		return row != 0 ? true : false;
	}
	
	@Override
	public <T> boolean deleteAll(Class<T> type) {
		SQLiteManager.getInstance();
		SQLiteDatabase db = getWritableDatabase();
		TableData data = SQLiteManager.mEntityDBTables.get(type);
		String tableName = data.getTableName();
		int row = db.delete(tableName, null, null);
		return row != 0 ? true : false;
	}
	
	@Override
	public boolean update(Object object, String id) {
		SQLiteManager.getInstance();
		if (object == null) {
			return false;
		}
		SQLiteDatabase db = getWritableDatabase();
		TableData data = SQLiteManager.mEntityDBTables.get(object.getClass());
		String tableName = data.getTableName();
		String natrualKey = data.getNatrualKey();
		if (natrualKey == null || natrualKey.length() == 0) {
			natrualKey = Constant.PRIMARY_KEY;
		}
		int row = db.update(tableName, generateContentValues(object), natrualKey + "=?", new String[]{id});
		return row != 0 ? true : false;
	}

	@Override
	public <T> T queryById(Class<T> type, String id) {
		SQLiteManager.getInstance();
		SQLiteDatabase db = getWritableDatabase();
		TableData data = SQLiteManager.mEntityDBTables.get(type);
		String tableName = data.getTableName();
		String natrualKey = data.getNatrualKey();
		if (natrualKey == null || natrualKey.length() == 0) {
			natrualKey = Constant.PRIMARY_KEY;
		}
		Set<String> columns = data.getColumnFields().keySet();
		
		Cursor cursor = db.query(tableName, columns.toArray(new String[] {}),
				natrualKey + "=?", new String[] { id }, null, null, null);
		List<T> objects = generateEntityFromCursor(type, data, cursor);
		if (objects != null && objects.size() > 0) {
			return objects.get(0);
		}
		return null;
	}

	@Override
	public <T> List<T> queryAll(Class<T> type) {
		SQLiteManager.getInstance();
		SQLiteDatabase db = getWritableDatabase();
		TableData data = SQLiteManager.mEntityDBTables.get(type);
		String tableName = data.getTableName();
		Set<String> columns = data.getColumnFields().keySet();
		
		Cursor cursor = db.query(tableName, columns.toArray(new String[] {}), null, null, null, null, null);
		return generateEntityFromCursor(type, data, cursor);
	}

	private <T> List<T> generateEntityFromCursor(Class<T> type, TableData data, Cursor cursor) {
		List<T> result = new ArrayList<T>();
		while (cursor.moveToNext()) {
			Map<String, Field> fieldsValue = data.getColumnFields();
			try {
				T object = type.newInstance();
				for (String columnName : fieldsValue.keySet()) {
					String column = cursor.getString(cursor.getColumnIndex(columnName));
				
					Field field = fieldsValue.get(columnName);
					boolean hasColumnAnno = field.isAnnotationPresent(Column.class);
					ColumnClass classType;
					if (hasColumnAnno) {
						classType = field.getAnnotation(Column.class).classType();
					} else {
						classType = ColumnClass.STRING;
					}
					setEntityFieldValue(object, field, classType, column);
				}
				result.add(object);
			} catch (Exception e) {
				android.util.Log.e("test", "generateEntityFromCursor exception: " + e.toString());
				continue;
			}
		}
		return result;
	}

	private void setEntityFieldValue(Object object, Field field, ColumnClass classType, String column) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		if (classType == ColumnClass.STRING) {
			field.set(object, column);
		} else if (classType == ColumnClass.BOOLEAN) {
			field.setBoolean(object, Util.text2Boolean(column));
		} else if (classType == ColumnClass.DOUBLE) {
			field.setDouble(object, Util.parseDouble(column));
		} else if (classType == ColumnClass.FLOAT) {
			field.setFloat(object, Util.parseFloat(column));
		} else if (classType == ColumnClass.INTEGER) {
			field.setInt(object, Util.parseInt(column));
		} else if (classType == ColumnClass.LONG) {
			field.setLong(object, Util.parseLong(column));
		}
	}

	private ContentValues generateContentValues(Object object) {
		ContentValues values = new ContentValues();
		TableData data = SQLiteManager.mEntityDBTables.get(object.getClass());
		Map<String, Field> fieldColumns = data.getColumnFields();
		for (String columnName : fieldColumns.keySet()) {
			try {
				Field field = fieldColumns.get(columnName);
				field.setAccessible(true);
				Object value = field.get(object);
				if (value == null) {
					value = "";
				}
				values.put(columnName, value.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return values;
	}

	public String toFirstLetterUpperCase(String str) {
		if (str == null || str.length() == 0) {
			return "";
		}
		if (str.length() == 1) {
			return str.toUpperCase();
		}
		String firstLetter = str.substring(0, 1).toUpperCase();
		return firstLetter + str.substring(1, str.length());
	}

}
