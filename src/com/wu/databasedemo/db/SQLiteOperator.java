package com.wu.databasedemo.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
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
import android.text.TextUtils;
import android.util.Log;

import com.wu.databasedemo.db.data.FieldData;
import com.wu.databasedemo.db.data.TableData;
import com.wu.databasedemo.db.helper.ForeignKey;

public class SQLiteOperator extends SQLiteOpenHelper implements
		ISQLiteOpenHelper {

	private static final String TAG = "db";

	private static SQLiteOperator instance;

	private SQLiteOperator(Context context) {
		super(context, SQLiteManager.DATABASE_NAME, null,
				SQLiteManager.DATABASE_VERSION);
	}

	public static SQLiteOperator getInstance(Context context) {
		if (instance == null) {
			instance = new SQLiteOperator(context);
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
			TableData tableData = SQLiteManager.mEntityDBTables.get(cl);
			String tableName = tableData.getTableName();
			List<FieldData> columns = tableData.getColumnFields();
			List<FieldData> foreigns = tableData.getForeignFields();
			if (Utility.size(columns) == 0 && Utility.size(foreigns) == 0) {
				continue;
			}

			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE IF NOT EXISTS " + tableName.toUpperCase()
					+ "(");
			sql.append(SQLiteManager.PRIMARY_KEY
					+ " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT");

			int size = 0;
			size = Utility.size(columns);
			for (int j = 0; j < size; j++) {
				sql.append(", " + columns.get(j).column + " TEXT");
			}

			size = Utility.size(foreigns);
			for (int j = 0; j < size; j++) {
				sql.append(", " + foreigns.get(j).column + " TEXT");
			}

			sql.append(")");
			Log.i(TAG, "build table sql: " + sql);
			try {
				db.execSQL(sql.toString());
			} catch (SQLException e) {
				result = false;
				Log.e(TAG, "build table exception: " + e.toString());
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
			String sql = "DROP TABLE IF EXISTS " + data.getTableName();
			db.execSQL(sql);
			Log.i(TAG, "exec DROP SQL: " + sql);
		}
	}

	@Override
	public boolean save(Object object) {
		if (object == null) {
			Log.i(TAG, "--> save: save null into database");
			return false;
		}
		SQLiteManager.getInstance();
		TableData tableData = SQLiteManager.mEntityDBTables.get(object
				.getClass());
		if (tableData == null) {
			Log.w(TAG, "--> save: no database table for Java Class -- "
					+ object.getClass());
			return false;
		}
		String tableName = tableData.getTableName();

		try {
			SQLiteDatabase db = getWritableDatabase();

			db.beginTransaction();
			List<FieldData> foreigns = tableData.getForeignFields();
			int size = foreigns.size();
			for (int i = 0; i < size; i++) {
				FieldData fieldData = foreigns.get(i);
				save(getFieldValue(object, fieldData.field));
			}
			long row = db.insert(tableName, null,
					generateContentValues(tableData, object));
			db.endTransaction();

			return row == -1 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "save exception: " + e.getMessage());
		}
		return false;
	}

	@Override
	public <T> boolean saveAll(List<T> objects) {
		if (objects == null || objects.size() == 0) {
			Log.i(TAG, "--> saveAll: save empty list into database");
			return false;
		}
		SQLiteManager.getInstance();
		boolean result = true;
		for (int i = 0; i < objects.size(); i++) {
			Object object = objects.get(i);
			if (!save(object)) {
				Log.w(TAG, "--> saveAll: save fail, " + object);
				result = false;
			}
		}
		return result;
	}

	@Override
	public <T> boolean deleteById(Class<T> type, String id) {
		if (TextUtils.isEmpty(id)) {
			Log.w(TAG, "--> deleteById: empty id to delete");
			return false;
		}
		SQLiteManager.getInstance();
		TableData tableData = SQLiteManager.mEntityDBTables.get(type);
		String tableName = tableData.getTableName();

		FieldData naturalKey = tableData.getNaturalKey();

		try {
			SQLiteDatabase db = getWritableDatabase();
			int row = db.delete(tableName, naturalKey.column + "=?",
					new String[] { id });
			return row == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "deleteById exception: " + e.getMessage());
		}
		return false;
	}

	@Override
	public <T> boolean delete(Class<T> type, String[] whereClause,
			String[] whereArgs) {
		SQLiteManager.getInstance();
		TableData tableData = SQLiteManager.mEntityDBTables.get(type);
		String tableName = tableData.getTableName();

		String whereCondition = null;
		if (whereClause != null && whereClause.length > 0) {
			whereCondition = new String();
			for (int i = 0; i < whereClause.length; i++) {
				if (i > 0) {
					whereCondition += " AND ";
				}
				whereCondition += SQLiteManager
						.getColumnFromField(whereClause[i]) + "=?";
			}
		}

		String[] whereValues = null;
		if (whereArgs != null && whereArgs.length > 0) {
			whereValues = whereArgs;
		}

		try {
			SQLiteDatabase db = getWritableDatabase();
			int row = db.delete(tableName, whereCondition, whereValues);
			return row == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "delete exception: " + e.getMessage());
		}
		return false;
	}

	@Override
	public boolean update(Object object, String id) {
		if (object == null) {
			Log.w(TAG, "--> update: update null into database");
			return false;
		}
		if (TextUtils.isEmpty(id)) {
			Log.w(TAG, "--> update: empty id to update");
			return false;
		}
		SQLiteManager.getInstance();
		TableData tableData = SQLiteManager.mEntityDBTables.get(object
				.getClass());
		String tableName = tableData.getTableName();

		FieldData naturalKey = tableData.getNaturalKey();

		try {
			SQLiteDatabase db = getWritableDatabase();
			int row = db.update(tableName,
					generateContentValues(tableData, object), naturalKey.column
							+ "=?", new String[] { id });
			return row == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "update exception: " + e.getMessage());
		}
		return false;
	}

	@Override
	public boolean update(Object object, String[] whereClause,
			String[] whereArgs) {
		if (object == null) {
			Log.w(TAG, "update: update null into database");
			return false;
		}
		SQLiteManager.getInstance();
		TableData tableData = SQLiteManager.mEntityDBTables.get(object
				.getClass());
		String tableName = tableData.getTableName();

		String whereCondition = null;
		if (whereClause != null && whereClause.length > 0) {
			whereCondition = new String();
			for (int i = 0; i < whereClause.length; i++) {
				if (i > 0) {
					whereCondition += " AND ";
				}
				whereCondition += SQLiteManager
						.getColumnFromField(whereClause[i]) + "=?";
			}
		}

		String[] whereValues = null;
		if (whereArgs != null && whereArgs.length > 0) {
			whereValues = whereArgs;
		}

		try {
			SQLiteDatabase db = getWritableDatabase();
			int row = db.update(tableName,
					generateContentValues(tableData, object), whereCondition,
					whereValues);
			return row == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "update exception: " + e.getMessage());
		}
		return false;
	}

	@Override
	public <T> T queryById(Class<T> type, String id) {
		if (TextUtils.isEmpty(id)) {
			Log.w(TAG, "update: empty id to query");
			return null;
		}
		SQLiteManager.getInstance();
		TableData tableData = SQLiteManager.mEntityDBTables.get(type);
		List<FieldData> columns = tableData.getColumnFields();
		String tableName = tableData.getTableName();

		FieldData naturalKey = tableData.getNaturalKey();

		Cursor cursor = null;
		try {
			SQLiteDatabase db = getWritableDatabase();
			cursor = db.query(tableName, null, naturalKey.column + "=?",
					new String[] { id }, null, null, null);
			List<T> objects = generateEntityFromCursor(type, tableData, cursor);
			if (objects != null && objects.size() > 0) {
				return objects.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "queryById exception: " + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	@Override
	public <T> List<T> query(Class<T> type, String[] selection,
			String[] selectionArgs) {
		SQLiteManager.getInstance();
		TableData tableData = SQLiteManager.mEntityDBTables.get(type);
		List<FieldData> columns = tableData.getColumnFields();
		String tableName = tableData.getTableName();

		String selCondition = null;
		if (selection != null && selection.length > 0) {
			selCondition = new String();
			for (int i = 0; i < selection.length; i++) {
				if (i > 0) {
					selCondition += " AND ";
				}
				selCondition += SQLiteManager.getColumnFromField(selection[i])
						+ "=?";
			}
		}

		String[] selValues = null;
		if (selectionArgs != null && selectionArgs.length > 0) {
			selValues = selectionArgs;
		}

		Cursor cursor = null;
		try {
			SQLiteDatabase db = getWritableDatabase();
			cursor = db.query(tableName, null, selCondition, selValues, null,
					null, null);
			return generateEntityFromCursor(type, tableData, cursor);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "query exception: " + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return new ArrayList<T>();
	}

	private <T> List<T> generateEntityFromCursor(Class<T> type,
			TableData tableData, Cursor cursor) {
		List<T> result = new ArrayList<T>();
		while (cursor.moveToNext()) {
			List<FieldData> fieldsValue = tableData.getColumnFields();
			try {
				T object = type.newInstance();
				int size = Utility.size(fieldsValue);
				for (int i = 0; i < size; i++) {
					FieldData fieldData = fieldsValue.get(i);
					String column = cursor.getString(cursor
							.getColumnIndex(fieldData.column));
					setEntityFieldValue(object, fieldData.field, column);
				}
				result.add(object);
			} catch (Exception e) {
				Log.e(TAG,
						"generateEntityFromCursor exception: " + e.toString());
				continue;
			}
		}
		return result.size() != 0 ? result : null;
	}

	private void setEntityFieldValue(Object object, Field field, String column)
			throws NullPointerException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		if (field.getType() == boolean.class) {
			field.setBoolean(object, Utility.text2Boolean(column));
		} else if (field.getType() == double.class) {
			field.setDouble(object, Utility.parseDouble(column));
		} else if (field.getType() == float.class) {
			field.setFloat(object, Utility.parseFloat(column));
		} else if (field.getType() == int.class) {
			field.setInt(object, Utility.parseInt(column));
		} else if (field.getType() == long.class) {
			field.setLong(object, Utility.parseLong(column));
		} else if (field.getType() == short.class) {
			field.setShort(object, Utility.parseShort(column));
		} else if (field.getType() == byte.class) {
			field.setByte(object, Utility.parseByte(column));
		} else if (field.getType() == char.class) {
			field.setChar(object, Utility.parseChar(column));
		} else {
			field.set(object, column);
		}
	}

	private ContentValues generateContentValues(TableData tableData,
			Object object) throws SecurityException, IllegalArgumentException,
			IllegalAccessException {
		ContentValues values = new ContentValues();

		List<FieldData> fieldColumns = tableData.getColumnFields();
		int size = 0;
		size = Utility.size(fieldColumns);
		for (int i = 0; i < size; i++) {
			FieldData fieldData = fieldColumns.get(i);
			values.put(fieldData.column,
					getFieldValueText(object, fieldData.field));
		}

		List<FieldData> foreignColumns = tableData.getForeignFields();
		size = Utility.size(foreignColumns);
		for (int i = 0; i < size; i++) {
			FieldData fieldData = foreignColumns.get(i);
			values.put(fieldData.column,
					getFieldValueText(object, fieldData.field));
		}

		return values;
	}

	private Object getFieldValue(Object object, Field field)
			throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		Object value = field.get(object);
		return value;
	}

	private String getFieldValueText(Object object, Field field)
			throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		Object value = field.get(object);
		if (value == null) {
			value = "";
		}
		return value.toString();
	}

}
