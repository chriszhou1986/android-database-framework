package com.wu.databasedemo.db;

import java.util.List;

public interface ISQLiteOpenHelper {

	boolean save(Object object);

	<T> boolean saveAll(List<T> objects);

	boolean update(Object object, String id);

	<T> boolean deleteById(Class<T> type, String id);

	<T> boolean deleteAll(Class<T> type);

	<T> T queryById(Class<T> type, String id);

	<T> List<T> queryAll(Class<T> type);

}
