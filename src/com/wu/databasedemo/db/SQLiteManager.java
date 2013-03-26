package com.wu.databasedemo.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.wu.databasedemo.db.data.TableData;
import com.wu.databasedemo.db.helper.NatrualKey;
import com.wu.databasedemo.db.helper.NotPersistent;
import com.wu.databasedemo.db.helper.Table;
import com.wu.databasedemo.entity.Person;

public class SQLiteManager {

	public static final int DATABASE_VERSION = 1;

	public static final String DATABASE_NAME = "test.db";

	public static final String PRIMARY_KEY = "_ID";

	public static SQLiteManager mManager;

	static Map<Class<?>, TableData> mEntityDBTables;

	static final Class<?>[] ENTITY_PERSISTENT = new Class<?>[] { Person.class };

	private SQLiteManager() {
		super();
	}

	public static SQLiteManager getInstance() {
		if (mManager == null) {
			mManager = new SQLiteManager();
			initEntityDBTables();
		}
		return mManager;
	}

	static void initEntityDBTables() {
		if (mEntityDBTables == null) {
			mEntityDBTables = new HashMap<Class<?>, TableData>();
		}
		mEntityDBTables.clear();

		for (int i = 0; i < ENTITY_PERSISTENT.length; i++) {
			Class<?> cl = ENTITY_PERSISTENT[i];
			Field[] fields = cl.getDeclaredFields();
			boolean isTable = cl.isAnnotationPresent(Table.class);
			if (isTable && fields.length > 0) {
				Table table = cl.getAnnotation(Table.class);
				String tableName = table.TableName();
				if (tableName != null && tableName.length() > 0) {
					TableData data = new TableData();
					Map<String, Field> columns = new HashMap<String, Field>();
					String natrualKey = null;
					for (int j = 0; j < fields.length; j++) {
						Field field = fields[j];
						boolean notPersistent = field
								.isAnnotationPresent(NotPersistent.class);
						if (!notPersistent) {
							boolean isNatrualKey = field
									.isAnnotationPresent(NatrualKey.class);
							if (isNatrualKey) {
								natrualKey = getColumnFromField(field);
							}
							columns.put(getColumnFromField(field), field);
						}
					}
					data.setTableName(tableName);
					data.setNatrualKey(natrualKey);
					data.setColumnFields(columns);
					mEntityDBTables.put(cl, data);
				}
			}
		}
	}

	public static void beginTransaction(Context context) {
		SQLiteOperator.getInstance(context).getWritableDatabase()
				.beginTransaction();
	}

	public static void endTransaction(Context context) {
		SQLiteOperator.getInstance(context).getWritableDatabase()
				.endTransaction();
	}

	public static String getColumnFromField(Field field) {
		if (field != null) {
			return field.getName().toUpperCase();
		}
		return "";
	}

	public static String getColumnFromField(String fieldName) {
		if (fieldName != null) {
			return fieldName.toUpperCase();
		}
		return "";
	}

	public static void close(Context context) {
		SQLiteOperator.getInstance(context).close();
	}

}
