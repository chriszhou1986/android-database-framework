package com.wu.databasedemo.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
			List<FieldData> normals = tableData.getNormalColumns();
			List<FieldData> foreigns = tableData.getForeignColumns();
			if (Utility.size(normals) == 0 && Utility.size(foreigns) == 0) {
				continue;
			}

			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE IF NOT EXISTS '" + tableName.toUpperCase()
					+ "' (");
			sql.append("'" + SQLiteManager.PRIMARY_KEY
					+ "' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT");

			int size = 0;
			size = Utility.size(normals);
			for (int j = 0; j < size; j++) {
				sql.append(", '" + normals.get(j).column + "' TEXT");
			}

			size = Utility.size(foreigns);
			for (int j = 0; j < size; j++) {
				sql.append(", '" + foreigns.get(j).column + "' TEXT");
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
			String sql = "DROP TABLE IF EXISTS '" + data.getTableName() + "'";
			db.execSQL(sql);
			Log.i(TAG, "exec DROP SQL: " + sql);
		}
	}

	@Override
	public boolean save(Object object) {
		if (object == null) {
			Log.w(TAG, "--> save: save null into database");
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

		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();
			db.beginTransaction();

			List<FieldData> foreigns = tableData.getForeignColumns();
			int size = foreigns.size();
			for (int i = 0; i < size; i++) {
				FieldData foreign = foreigns.get(i);
				save(getFieldValue(object, foreign.field));
			}
			long row = db.insert(tableName, null,
					generateContentValues(tableData, object));

			if (row != -1) {
				db.setTransactionSuccessful();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "save exception: " + e.getMessage());
		} finally {
			endTransaction(db);
		}
		return false;
	}

	@Override
	public <T> boolean saveAll(List<T> objects) {
		if (objects == null || objects.size() == 0) {
			Log.w(TAG, "--> saveAll: save empty list into database");
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
	public boolean updateById(Object object) {
		if (object == null) {
			Log.w(TAG, "--> update: update null into database");
			return false;
		}
		SQLiteManager.getInstance();
		TableData tableData = SQLiteManager.mEntityDBTables.get(object
				.getClass());
		FieldData naturalKey = tableData.getNaturalKey();

		try {
			String naturalValue = getFieldValueText(object, naturalKey.field);
			if (naturalValue == null || naturalValue.length() == 0) {
				throw new Exception(
						"The data to update must be have an effective natural-key value.");
			}
			String whereClause = naturalKey.column + "=?";
			String[] whereArgs = new String[] { naturalValue };
			return updateData(object, tableData, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
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

		return updateData(object, tableData, whereCondition, whereValues);
	}

	private boolean updateData(Object object, TableData tableData,
			String whereClause, String[] whereArgs) {

		String tableName = tableData.getTableName();
		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();
			db.beginTransaction();

			int row = db.update(tableName,
					generateContentValues(tableData, object), whereClause,
					whereArgs);

			boolean updateForeignSuccess = updateForeignData(object, tableData);

			if (row != 0 || updateForeignSuccess) {
				db.setTransactionSuccessful();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "update exception: " + e.getMessage());
		} finally {
			endTransaction(db);
		}
		return false;
	}

	private boolean updateForeignData(Object object, TableData tableData)
			throws IllegalArgumentException, IllegalAccessException {
		boolean updateForeignSuccess = true;
		List<FieldData> foreigns = tableData.getForeignColumns();
		int foreignSize = Utility.size(foreigns);
		for (int i = 0; i < foreignSize; i++) {
			FieldData foreign = foreigns.get(i);
			if (!updateById(getFieldValue(object, foreign.field))) {
				updateForeignSuccess = false;
			}
		}
		return updateForeignSuccess;
	}

	@Override
	public <T> T queryById(Class<T> type, String id) {
		if (TextUtils.isEmpty(id)) {
			Log.w(TAG, "update: empty id to query");
			return null;
		}
		SQLiteManager.getInstance();
		TableData tableData = SQLiteManager.mEntityDBTables.get(type);
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
		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();

			cursor = db.query(tableName, null, selCondition, selValues, null,
					null, null);

			List<T> results = generateEntityFromCursor(type, tableData, cursor);
			return results;
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
		List<FieldData> normalColumns = tableData.getNormalColumns();
		int normalSize = Utility.size(normalColumns);

		List<FieldData> foreigns = tableData.getForeignColumns();
		int foreignSize = foreigns.size();

		List<T> results = new ArrayList<T>();
		while (cursor.moveToNext()) {
			try {
				T object = type.newInstance();
				for (int i = 0; i < normalSize; i++) {
					FieldData fieldData = normalColumns.get(i);
					String value = cursor.getString(cursor
							.getColumnIndex(fieldData.column));
					setEntityFieldValue(object, fieldData.field, value);
				}
				for (int i = 0; i < foreignSize; i++) {
					FieldData foreign = foreigns.get(i);
					String value = cursor.getString(cursor
							.getColumnIndex(foreign.column));
					Object foreignValue = queryById(foreign.field.getType(),
							value);
					setEntityFieldValue(object, foreign.field, foreignValue);
				}
				results.add(object);
			} catch (Exception e) {
				continue;
			}
		}
		return results;
	}

	private void setEntityFieldValue(Object object, Field field, Object value)
			throws NullPointerException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		final String valueString;
		if (value == null) {
			valueString = "";
		} else {
			valueString = value.toString();
		}

		field.setAccessible(true);

		if (field.getType() == boolean.class) {
			field.setBoolean(object, Utility.text2Boolean(valueString));
		} else if (field.getType() == double.class) {
			field.setDouble(object, Utility.parseDouble(valueString));
		} else if (field.getType() == float.class) {
			field.setFloat(object, Utility.parseFloat(valueString));
		} else if (field.getType() == int.class) {
			field.setInt(object, Utility.parseInt(valueString));
		} else if (field.getType() == long.class) {
			field.setLong(object, Utility.parseLong(valueString));
		} else if (field.getType() == short.class) {
			field.setShort(object, Utility.parseShort(valueString));
		} else if (field.getType() == byte.class) {
			field.setByte(object, Utility.parseByte(valueString));
		} else if (field.getType() == char.class) {
			field.setChar(object, Utility.parseChar(valueString));
		} else {
			field.set(object, value);
		}
	}

	private ContentValues generateContentValues(TableData tableData,
			Object object) throws SecurityException, IllegalArgumentException,
			IllegalAccessException {

		ContentValues values = new ContentValues();
		List<FieldData> normalColums = tableData.getNormalColumns();
		int size = 0;
		size = Utility.size(normalColums);
		for (int i = 0; i < size; i++) {
			FieldData normalColumn = normalColums.get(i);
			String value = getFieldValueText(object, normalColumn.field);
			if (value != null) {
				values.put(normalColumn.column, value);
			}
		}

		List<FieldData> foreignColumns = tableData.getForeignColumns();
		size = Utility.size(foreignColumns);
		for (int i = 0; i < size; i++) {
			FieldData foreign = foreignColumns.get(i);
			String value = getForeignValueText(object, foreign.field);
			if (value != null) {
				values.put(foreign.column, value);
			}
		}

		return values;
	}

	private String getForeignValueText(Object object, Field field)
			throws IllegalArgumentException, IllegalAccessException {

		TableData foreignData = SQLiteManager.mEntityDBTables.get(field
				.getType());
		Field foreignNaturalKey = foreignData.getNaturalKey().field;

		field.setAccessible(true);
		foreignNaturalKey.setAccessible(true);

		Object foreignValue = field.get(object);
		if (foreignValue == null) {
			return null;
		}
		Object result = foreignNaturalKey.get(foreignValue);

		if (result == null) {
			return null;
		}
		return result.toString();
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
			return null;
		}
		return value.toString();
	}

	private void endTransaction(SQLiteDatabase db) {
		if (db != null) {
			db.endTransaction();
		}
	}

}
