package com.wu.databasedemo.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.wu.databasedemo.db.data.FieldData;
import com.wu.databasedemo.db.data.TableData;
import com.wu.databasedemo.db.exception.NaturalKeyRepetitiveException;
import com.wu.databasedemo.db.exception.NoNaturalKeyException;
import com.wu.databasedemo.db.exception.TableNameInvalidException;
import com.wu.databasedemo.db.helper.ForeignKey;
import com.wu.databasedemo.db.helper.NatrualKey;
import com.wu.databasedemo.db.helper.NotPersistent;
import com.wu.databasedemo.db.helper.Table;
import com.wu.databasedemo.entity.Address;
import com.wu.databasedemo.entity.Person;

public class SQLiteManager {

	public static final int DATABASE_VERSION = 11;

	public static final String DATABASE_NAME = "test.db";

	public static final String PRIMARY_KEY = "_ID";

	private static final String FOREIGN_COLUMN_SUFFIX = "_FK";

	static final Class<?>[] ENTITY_PERSISTENT = new Class<?>[] { Person.class,
			Address.class };
	
	private static final String SERIAL_VERSION_UID = "serialVersionUID";

	static Map<Class<?>, TableData> mEntityDBTables;
	
	private static SQLiteManager mManager;

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
				if (tableName == null || tableName.length() == 0) {
					throw new TableNameInvalidException(
							"The tableName can not be empty.");
				}

				TableData data = new TableData();
				FieldData naturalKey = null;
				boolean foundNaturalKey = false;
				for (int j = 0; j < fields.length; j++) {
					Field field = fields[j];
					if (isSerialVersion(field)) {
						continue;
					}
					boolean isNotPersistent = field
							.isAnnotationPresent(NotPersistent.class);
					boolean isForeignKey = field
							.isAnnotationPresent(ForeignKey.class);
					boolean isNormalColumn = !isNotPersistent && !isForeignKey;

					// process foreign-key attributes
					if (isForeignKey) {
						data.addForeignColumn(getForeignFromField(field), field);
					}

					// process normal persistent attributes
					if (isNormalColumn) {
						boolean isNatrualKey = field
								.isAnnotationPresent(NatrualKey.class);
						if (isNatrualKey) {
							if (foundNaturalKey) {
								throw new NaturalKeyRepetitiveException(
										"The persistent Class must be just have one natural-key.");
							}
							foundNaturalKey = true;
							naturalKey = new FieldData();
							naturalKey.column = getColumnFromField(field);
							naturalKey.field = field;
						}
						data.addNormalColumn(getColumnFromField(field), field);
					}
				}
				if (naturalKey == null) {
					throw new NoNaturalKeyException(
							"The persistent Class must be have one natural-key.");
				}
				data.setNaturalKey(naturalKey);
				data.setTableName(tableName);
				mEntityDBTables.put(cl, data);
			}
		}
	}

	private static boolean isSerialVersion(Field field) {
		return SERIAL_VERSION_UID.equals(field.getName());
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

	public static String getForeignFromField(Field field) {
		if (field != null) {
			return field.getName().toUpperCase() + FOREIGN_COLUMN_SUFFIX;
		}
		return "";
	}

	public static void close(Context context) {
		SQLiteOperator.getInstance(context).close();
	}

}
