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
			Set<String> columns = tableData.getColumnFields().keySet();
			if (columns == null || columns.size() == 0) {
				continue;
			}

			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE IF NOT EXISTS " + tableName.toUpperCase()
					+ "(");
			sql.append(SQLiteManager.PRIMARY_KEY
					+ " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT");

			Iterator<String> it = columns.iterator();
			while (it.hasNext()) {
				sql.append(", " + it.next() + " TEXT");
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
			db.execSQL("DROP TABLE IF EXISTS " + data.getTableName());
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
			long row = db.insert(tableName, null,
					generateContentValues(tableData, object));
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
		String natrualKey = tableData.getNatrualKey();
		if (natrualKey == null || natrualKey.length() == 0) {
			natrualKey = SQLiteManager.PRIMARY_KEY;
		}

		try {
			SQLiteDatabase db = getWritableDatabase();
			int row = db.delete(tableName, natrualKey + "=?",
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
		String natrualKey = tableData.getNatrualKey();
		if (natrualKey == null || natrualKey.length() == 0) {
			natrualKey = SQLiteManager.PRIMARY_KEY;
		}

		try {
			SQLiteDatabase db = getWritableDatabase();
			int row = db.update(tableName,
					generateContentValues(tableData, object),
					natrualKey + "=?", new String[] { id });
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
		Set<String> columns = tableData.getColumnFields().keySet();
		String tableName = tableData.getTableName();
		String natrualKey = tableData.getNatrualKey();
		if (TextUtils.isEmpty(natrualKey)) {
			natrualKey = SQLiteManager.PRIMARY_KEY;
		}

		Cursor cursor = null;
		try {
			SQLiteDatabase db = getWritableDatabase();
			cursor = db.query(tableName, columns.toArray(new String[] {}),
					natrualKey + "=?", new String[] { id }, null, null, null);
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
		Set<String> columns = tableData.getColumnFields().keySet();
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
			cursor = db.query(tableName, columns.toArray(new String[] {}),
					selCondition, selValues, null, null, null);
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

	private <T> List<T> generateEntityFromCursor(Class<T> type, TableData data,
			Cursor cursor) {
		List<T> result = new ArrayList<T>();
		while (cursor.moveToNext()) {
			Map<String, Field> fieldsValue = data.getColumnFields();
			try {
				T object = type.newInstance();
				for (String columnName : fieldsValue.keySet()) {
					String column = cursor.getString(cursor
							.getColumnIndex(columnName));

					Field field = fieldsValue.get(columnName);
					setEntityFieldValue(object, field, column);
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
		Map<String, Field> fieldColumns = tableData.getColumnFields();
		for (String columnName : fieldColumns.keySet()) {
			Field field = fieldColumns.get(columnName);
			field.setAccessible(true);
			Object value = field.get(object);
			if (value == null) {
				value = "";
			}
			values.put(columnName, value.toString());
		}
		return values;
	}

}
