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
	 * �����趨��ҵ������ɾ����Ӧ�ļ�¼��
	 * 
	 * @param type
	 *            Ҫɾ�������ݵ����͡�
	 * @param id
	 *            ��¼��ҵ������ֵ��
	 * @return ɾ���ɹ����� true�����򷵻� false��
	 */
	<T> boolean deleteById(Class<T> type, String id);

	/**
	 * ���ݸ���������ɾ�����������ļ�¼����� whereClause��whereArgs Ϊ
	 * empty��null��length==0��ʱ��ɾ�����м�¼��
	 * 
	 * @param type
	 *            Ҫɾ�������ݵ����͡�
	 * @param whereClause
	 *            type ����������������ִ�Сд���൱�� "WHERE SELECTION=?" �е�
	 *            "SELECTION"��Ϊemptyʱɾ�����м�¼��
	 * @param whereArgs
	 *            �൱�� "WHERE SELECTION=?" �е� "?"��Ϊ empty ʱɾ�����м�¼��
	 * @return ɾ���ɹ����� true�����򷵻� false��
	 */
	<T> boolean delete(Class<T> type, String[] whereClause, String[] whereArgs);

	/**
	 * �����趨��ҵ��������ѯ��Ӧ�ļ�¼��
	 * 
	 * @param type
	 *            Ҫ��ѯ�����ݵ����͡�
	 * @param id
	 *            ��¼��ҵ������ֵ��
	 * @return ��û�з��������ļ�¼���� null��
	 */
	<T> T queryById(Class<T> type, String id);

	/**
	 * ���ݸ�����������ѯ���������ļ�¼����� selection��selectionArgs Ϊ
	 * empty��null��length==0��ʱ���ѯ���м�¼��
	 * 
	 * @param type
	 * @param selection
	 *            type ����������������ִ�Сд���൱�� "WHERE SELECTION=?" �е� "SELECTION"��Ϊ
	 *            empty ʱ��ѯ���м�¼��
	 * @param selectionArgs
	 *            �൱�� "WHERE SELECTION=?" �е� "?"��Ϊ empty ʱ��ѯ���м�¼��
	 * @return ��û�з��������ļ�¼���� null��
	 */
	<T> List<T> query(Class<T> type, String[] selection, String[] selectionArgs);

}
