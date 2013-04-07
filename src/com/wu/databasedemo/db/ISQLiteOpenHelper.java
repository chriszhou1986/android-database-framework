package com.wu.databasedemo.db;

import java.util.List;

public interface ISQLiteOpenHelper {

	/**
	 * Save the data to database.
	 * 
	 * @param object
	 *            the data to save.
	 * @return if success return true, otherwise return false.
	 */
	boolean save(Object object);

	/**
	 * Save the data's list to database.
	 * 
	 * @param objects
	 *            the data to save.
	 * @return if all success return true, otherwise return false.
	 */
	<T> boolean saveAll(List<T> objects);

	/**
	 * Update the data in database base on the neutral-key of the specified
	 * data.
	 * 
	 * @param object
	 *            the data to update.
	 * @return if success return true, otherwise return false.
	 */
	boolean updateById(Object object);

	/**
	 * Update data base on the specified condition(if the 'whereClause' and
	 * 'whereArgs' are null or they length are zero, to update all cursor).
	 * 
	 * @param object
	 *            the data to update.
	 * @param whereClause
	 *            the attribute in Class of data, and it ignore case(it is the
	 *            "SELECTION" in "WHERE SELECTION=?"). if it is empty delete all
	 *            cursor.
	 * @param whereArgs
	 *            it is the "?" in "WHERE SELECTION=?". if it is empty delete
	 *            all cursor.
	 * @return if success return true, otherwise return false.
	 */
	boolean update(Object object, String[] whereClause, String[] whereArgs);

	/**
	 * Delete data base on the specified natural-key value.
	 * 
	 * @param type
	 *            the Class of data to delete.
	 * @param id
	 *            the value of the natural-key.
	 * @return if success return true, otherwise return false.
	 */
	<T> boolean deleteById(Class<T> type, String id);

	/**
	 * Delete data base on the specified condition(if the 'whereClause' and
	 * 'whereArgs' are null or they length are zero, to delete all cursor).
	 * 
	 * @param type
	 *            the Class type of data to delete.
	 * @param whereClause
	 *            the attribute in Class of data, and it ignore case(it is the
	 *            "SELECTION" in "WHERE SELECTION=?"). if it is empty delete all
	 *            cursor.
	 * @param whereArgs
	 *            it is the "?" in "WHERE SELECTION=?". if it is empty delete
	 *            all cursor.
	 * @return if all success return true, otherwise return false.
	 */
	<T> boolean delete(Class<T> type, String[] whereClause, String[] whereArgs);

	/**
	 * Query data base on the specified natural-key value.
	 * 
	 * @param type
	 *            the Class type of data to query.
	 * @param id
	 *            the value of natural-key.
	 * @return if no record return null.
	 */
	<T> T queryById(Class<T> type, String id);

	/**
	 * Query data base on the specified condition(if the 'whereClause' and
	 * 'whereArgs' are null or they length are zero, to query all cursor).
	 * 
	 * @param type
	 *            the Class type of data to query.
	 * @param selection
	 *            the attribute in Class of data, and it ignore case(it is the
	 *            "SELECTION" in "WHERE SELECTION=?"). if it is empty delete all
	 *            cursor.
	 * @param selectionArgs
	 *            it is the "?" in "WHERE SELECTION=?". if it is empty delete
	 *            all cursor.
	 * @return if no record return null.
	 */
	<T> List<T> query(Class<T> type, String[] selection, String[] selectionArgs);

}
