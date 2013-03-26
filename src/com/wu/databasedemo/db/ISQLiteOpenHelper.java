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
	 * @param id
	 *            the value of data's neutral-key.
	 * @return if success return true, otherwise return false.
	 */
	boolean update(Object object, String id);

	/**
	 * Update data base on the specified condition(if the 'whereClause' and
	 * 'whereArgs' are null or it's length is zero, to delete all cursor).
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
	 * 根据设定的业务主键删除对应的记录。
	 * 
	 * @param type
	 *            要删除的数据的类型。
	 * @param id
	 *            记录的业务主键值。
	 * @return 删除成功返回 true，否则返回 false。
	 */
	<T> boolean deleteById(Class<T> type, String id);

	/**
	 * 根据给定的条件删除符合条件的记录。如果 whereClause、whereArgs 为
	 * empty（null或length==0）时则删除所有记录。
	 * 
	 * @param type
	 *            要删除的数据的类型。
	 * @param whereClause
	 *            type 类的属性名，不区分大小写。相当于 "WHERE SELECTION=?" 中的
	 *            "SELECTION"。为empty时删除所有记录。
	 * @param whereArgs
	 *            相当于 "WHERE SELECTION=?" 中的 "?"。为 empty 时删除所有记录。
	 * @return 删除成功返回 true，否则返回 false。
	 */
	<T> boolean delete(Class<T> type, String[] whereClause, String[] whereArgs);

	/**
	 * 根据设定的业务主键查询对应的记录。
	 * 
	 * @param type
	 *            要查询的数据的类型。
	 * @param id
	 *            记录的业务主键值。
	 * @return 若没有符合条件的记录返回 null。
	 */
	<T> T queryById(Class<T> type, String id);

	/**
	 * 根据给定的条件查询符合条件的记录。如果 selection、selectionArgs 为
	 * empty（null或length==0）时则查询所有记录。
	 * 
	 * @param type
	 * @param selection
	 *            type 类的属性名，不区分大小写。相当于 "WHERE SELECTION=?" 中的 "SELECTION"。为
	 *            empty 时查询所有记录。
	 * @param selectionArgs
	 *            相当于 "WHERE SELECTION=?" 中的 "?"。为 empty 时查询所有记录。
	 * @return 若没有符合条件的记录返回 null。
	 */
	<T> List<T> query(Class<T> type, String[] selection, String[] selectionArgs);

}
